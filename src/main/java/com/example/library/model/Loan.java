package com.example.library.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book-id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user-id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate loanDate;

    private LocalDate returnDate;
}