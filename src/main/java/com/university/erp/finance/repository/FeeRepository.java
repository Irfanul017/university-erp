package com.university.erp.finance.repository;

import com.university.erp.finance.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    boolean existsByFeeTypeIgnoreCase(String feeType);

    boolean existsByFeeTypeIgnoreCaseAndIdNot(String feeType, Long id);

    Optional<Fee> findByFeeTypeIgnoreCase(String feeType);

    List<Fee> findByActive(boolean active);

    List<Fee> findByFeeTypeContainingIgnoreCase(String feeType);

    List<Fee> findByFeeTypeContainingIgnoreCaseAndActive(String feeType, boolean active);
}
