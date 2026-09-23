package com.university.erp.common;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

/** Shared REST implementation for simple ERP master-data resources. */
public abstract class CrudController<T, ID> {
    private final JpaRepository<T, ID> repository;

    protected CrudController(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<T> create(@RequestBody T item) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(item));
    }

    @GetMapping
    public List<T> all() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public T one(@PathVariable ID id) {
        return find(id);
    }

    @PutMapping("/{id}")
    public T replace(@PathVariable ID id, @RequestBody T item) {
        T existing = find(id);
        BeanUtils.copyProperties(item, existing, "id", "createdAt", "updatedAt");
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable ID id) {
        try {
            repository.delete(find(id));
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Record is referenced by another resource");
        }
    }

    private T find(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found: " + id));
    }
}
