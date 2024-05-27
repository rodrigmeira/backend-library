package com.example.library.controller;

import com.example.library.dto.LoanDTO;
import com.example.library.model.Loan;
import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.service.LoanService;
import com.example.library.service.BookService;
import com.example.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<LoanDTO> getAllLoans() {
        return loanService.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long id) {
        Optional<Loan> loan = loanService.findById(id);
        return loan.map(l -> ResponseEntity.ok(convertToDto(l))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createLoan(@RequestBody LoanDTO loanDTO) {
        if (loanDTO.getBookId() == null || loanDTO.getUserId() == null) {
            return ResponseEntity.badRequest().body("Book ID and User ID are required.");
        }

        Optional<Book> book = bookService.findById(loanDTO.getBookId());
        if (!book.isPresent()) {
            return ResponseEntity.badRequest().body("Book not found with ID: " + loanDTO.getBookId());
        }

        Optional<User> user = userService.findById(loanDTO.getUserId());
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("User not found with ID: " + loanDTO.getUserId());
        }

        Loan loan = convertToEntity(loanDTO, book.get(), user.get());
        Loan savedLoan = loanService.save(loan);
        return ResponseEntity.ok(convertToDto(savedLoan).toString());
    }

    private LoanDTO convertToDto(Loan loan) {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(loan.getId());
        loanDTO.setBookId(loan.getBook().getId());
        loanDTO.setUserId(loan.getUser().getId());
        loanDTO.setLoanDate(loan.getLoanDate());
        loanDTO.setReturnDate(loan.getReturnDate());
        return loanDTO;
    }

    private Loan convertToEntity(LoanDTO loanDTO, Book book, User user) {
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setLoanDate(loanDTO.getLoanDate());
        loan.setReturnDate(loanDTO.getReturnDate());
        return loan;
    }
}
