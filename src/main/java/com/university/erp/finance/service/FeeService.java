package com.university.erp.finance.service;

import com.university.erp.finance.dto.FeeRequest;
import com.university.erp.finance.dto.FeeResponse;

import java.util.List;

public interface FeeService {

    FeeResponse create(FeeRequest request);

    FeeResponse get(Long id);

    List<FeeResponse> list(String feeType, Boolean active);

    FeeResponse update(Long id, FeeRequest request);

    FeeResponse setActive(Long id, boolean active);

    void delete(Long id);
}
