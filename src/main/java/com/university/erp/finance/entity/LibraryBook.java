package com.university.erp.finance.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "library_book", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isbn", nullable = false, unique = true)
    private String isbn;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "total_copies")
    @Builder.Default
    private Integer totalCopies = 0;

    @Column(name = "available_copies")
    @Builder.Default
    private Integer availableCopies = 0;
}
