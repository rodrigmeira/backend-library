package com.example.library.controller;

import com.example.library.dto.LoanDTO;
import com.example.library.dto.BookDTO;
import com.example.library.dto.UserDTO;
import com.example.library.model.Loan;
import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.service.BookService;
import com.example.library.service.UserService;
import com.example.library.service.LoanService;
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
    public LoanDTO createLoan(@RequestBody LoanDTO loanDTO) {
        Loan loan = convertToEntity(loanDTO);
        return convertToDto(loanService.save(loan));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> updateLoan(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        Optional<Loan> optionalLoan = loanService.findById(id);
        if (optionalLoan.isPresent()) {
            Loan loan = optionalLoan.get();
            loan.setBook(bookService.findById(loanDTO.getBook().getId()).orElseThrow(() -> new RuntimeException("Book not found")));
            loan.setUser(userService.findById(loanDTO.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found")));
            loan.setLoanDate(loanDTO.getLoanDate());
            loan.setReturnDate(loanDTO.getReturnDate());
            return ResponseEntity.ok(convertToDto(loanService.save(loan)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        if (loanService.findById(id).isPresent()) {
            loanService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private LoanDTO convertToDto(Loan loan) {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setId(loan.getId());
        loanDTO.setBook(convertToDto(loan.getBook()));
        loanDTO.setUser(convertToDto(loan.getUser()));
        loanDTO.setLoanDate(loan.getLoanDate());
        loanDTO.setReturnDate(loan.getReturnDate());
        return loanDTO;
    }

    private Loan convertToEntity(LoanDTO loanDTO) {
        Loan loan = new Loan();
        loan.setId(loanDTO.getId());
        loan.setBook(bookService.findById(loanDTO.getBook().getId()).orElseThrow(() -> new RuntimeException("Book not found")));
        loan.setUser(userService.findById(loanDTO.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found")));
        loan.setLoanDate(loanDTO.getLoanDate());
        loan.setReturnDate(loanDTO.getReturnDate());
        return loan;
    }

    private BookDTO convertToDto(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setIsbn(book.getIsbn());
        bookDTO.setAvailable(book.isAvailable());
        return bookDTO;
    }

    private UserDTO convertToDto(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }
}
