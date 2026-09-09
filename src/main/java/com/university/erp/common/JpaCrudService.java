package com.university.erp.common;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Reusable persistence service used by resource-specific service adapters. */
public class JpaCrudService<T, ID> implements CrudService<T, ID> {
    private final JpaRepository<T, ID> repository;

    public JpaCrudService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found: " + id));
    }

    @Override
    public T update(ID id, T entity) {
        T existing = findById(id);
        BeanUtils.copyProperties(entity, existing, "id", "createdAt", "updatedAt");
        return repository.save(existing);
    }

    @Override
    public void delete(ID id) {
        try {
            repository.delete(findById(id));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Record is referenced by another resource");
        }
    }
}
