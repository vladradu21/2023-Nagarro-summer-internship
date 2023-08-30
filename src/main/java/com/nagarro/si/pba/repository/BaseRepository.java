package com.nagarro.si.pba.repository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.function.Supplier;

@Repository
public class BaseRepository {
    protected <T> Optional<T> handleEmptyResult(Supplier<Optional<T>> query) {
        try {
            return query.get();
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}