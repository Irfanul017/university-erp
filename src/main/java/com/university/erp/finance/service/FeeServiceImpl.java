package com.university.erp.finance.service;

import com.university.erp.finance.dto.FeeRequest;
import com.university.erp.finance.dto.FeeResponse;
import com.university.erp.finance.entity.Fee;
import com.university.erp.finance.mapper.FeeMapper;
import com.university.erp.finance.repository.FeeRepository;
import com.university.erp.finance.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final PaymentRepository paymentRepository;

    public FeeServiceImpl(FeeRepository feeRepository, PaymentRepository paymentRepository) {
        this.feeRepository = feeRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public FeeResponse create(FeeRequest request) {
        if (request == null) {
            throw badRequest("Request body must not be null");
        }
        if (request.feeType() == null || request.feeType().trim().isEmpty()) {
            throw badRequest("Fee type is required");
        }
        String feeType = request.feeType().trim();
        if (feeRepository.existsByFeeTypeIgnoreCase(feeType)) {
            throw conflict("Fee definition with type '" + feeType + "' already exists");
        }
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("Fee amount must be greater than zero");
        }

        Fee fee = FeeMapper.toEntity(request);
        fee.setFeeType(feeType);
        return FeeMapper.toResponse(feeRepository.save(fee));
    }

    @Override
    @Transactional(readOnly = true)
    public FeeResponse get(Long id) {
        return FeeMapper.toResponse(findFee(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeResponse> list(String feeType, Boolean active) {
        String cleanType = (feeType != null && !feeType.trim().isEmpty()) ? feeType.trim() : null;
        List<Fee> result;
        if (cleanType != null && active != null) {
            result = feeRepository.findByFeeTypeContainingIgnoreCaseAndActive(cleanType, active);
        } else if (cleanType != null) {
            result = feeRepository.findByFeeTypeContainingIgnoreCase(cleanType);
        } else if (active != null) {
            result = feeRepository.findByActive(active);
        } else {
            result = feeRepository.findAll();
        }
        return result.stream().map(FeeMapper::toResponse).toList();
    }

    @Override
    public FeeResponse update(Long id, FeeRequest request) {
        if (request == null) {
            throw badRequest("Request body must not be null");
        }
        Fee existing = findFee(id);

        if (request.feeType() != null && !request.feeType().trim().isEmpty()) {
            String newType = request.feeType().trim();
            if (feeRepository.existsByFeeTypeIgnoreCaseAndIdNot(newType, id)) {
                throw conflict("Another fee definition with type '" + newType + "' already exists");
            }
            existing.setFeeType(newType);
        }

        if (request.amount() != null) {
            if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                throw badRequest("Fee amount must be greater than zero");
            }
            existing.setAmount(request.amount());
        }

        if (request.description() != null) {
            existing.setDescription(request.description().trim());
        }

        if (request.active() != null) {
            existing.setActive(request.active());
        }

        return FeeMapper.toResponse(feeRepository.save(existing));
    }

    @Override
    public FeeResponse setActive(Long id, boolean active) {
        Fee existing = findFee(id);
        existing.setActive(active);
        return FeeMapper.toResponse(feeRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Fee existing = findFee(id);
        if (paymentRepository.existsByFeeTypeIgnoreCase(existing.getFeeType())) {
            throw conflict("Fee definition '" + existing.getFeeType() + "' is referenced by existing payment records; deactivate it instead of deleting");
        }
        feeRepository.delete(existing);
    }

    private Fee findFee(Long id) {
        if (id == null) {
            throw badRequest("Fee ID must not be null");
        }
        return feeRepository.findById(id)
                .orElseThrow(() -> notFound("Fee definition not found with ID: " + id));
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
