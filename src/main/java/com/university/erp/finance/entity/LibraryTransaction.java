package com.university.erp.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "library_transaction")
@Getter
@Setter
@NoArgsConstructor
public class LibraryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long bookId;
    @Column(nullable = false)
    private UUID studentId;
    @Column(nullable = false)
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status = "ISSUED";
}
