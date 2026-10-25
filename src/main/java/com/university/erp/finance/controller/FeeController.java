package com.university.erp.finance.controller;

import com.university.erp.finance.dto.FeeRequest;
import com.university.erp.finance.dto.FeeResponse;
import com.university.erp.finance.service.FeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @PostMapping
    public ResponseEntity<FeeResponse> create(@RequestBody FeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feeService.create(request));
    }

    @GetMapping("/{id}")
    public FeeResponse get(@PathVariable Long id) {
        return feeService.get(id);
    }

    @GetMapping
    public List<FeeResponse> list(
            @RequestParam(required = false) String feeType,
            @RequestParam(required = false) Boolean active) {
        return feeService.list(feeType, active);
    }

    @PutMapping("/{id}")
    public FeeResponse update(@PathVariable Long id, @RequestBody FeeRequest request) {
        return feeService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public FeeResponse setActive(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean active,
            @RequestBody(required = false) Map<String, Boolean> body) {
        boolean newStatus = true;
        if (active != null) {
            newStatus = active;
        } else if (body != null && body.containsKey("active")) {
            newStatus = Boolean.TRUE.equals(body.get("active"));
        }
        return feeService.setActive(id, newStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        feeService.delete(id);
    }
}
