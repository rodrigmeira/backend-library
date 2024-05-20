package com.example.library.controller;

import com.example.library.dto.BookDTO;
import com.example.library.model.Book;
import com.example.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.findById(id);
        return book.map(b -> ResponseEntity.ok(convertToDto(b))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO bookDTO) {
        Book book = convertToEntity(bookDTO);
        return convertToDto(bookService.save(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(bookDTO.getTitle());
            book.setAuthor(bookDTO.getAuthor());
            book.setIsbn(bookDTO.getIsbn());
            book.setAvailable(bookDTO.isAvailable());
            return ResponseEntity.ok(convertToDto(bookService.save(book)));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (bookService.findById(id).isPresent()) {
            bookService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    public List<BookDTO> searchBooks(@RequestParam String title) {
        return bookService.searchByTitle(title).stream().map(this::convertToDto).collect(Collectors.toList());
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

    private Book convertToEntity(BookDTO bookDTO) {
        Book book = new Book();
        book.setId(bookDTO.getId());
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setAvailable(bookDTO.isAvailable());
        return book;
    }
}
