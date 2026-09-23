package com.university.erp.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;

@Entity
@Table(name = "fee")
@Getter
@Setter
@NoArgsConstructor
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String feeType;
    @Column(nullable = false)
    private BigDecimal amount;
    private String description;
    private boolean active = true;
}
