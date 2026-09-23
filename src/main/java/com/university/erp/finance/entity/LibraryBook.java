package com.university.erp.finance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "library_book")
@Getter
@Setter
@NoArgsConstructor
public class LibraryBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String isbn;
    @Column(nullable = false)
    private String title;
    private String author;
    private String publisher;
    private Integer totalCopies = 0;
    private Integer availableCopies = 0;
}
