package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.File;

import java.util.Collection;
import java.util.Optional;

public interface FileRepository {
    Optional<File> save(File file);

    Optional<File> findById(int id);

    boolean deleteById(int id);

    Collection<File> findAll();
}
