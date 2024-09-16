package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreRepository {

    Optional<Genre> save(Genre genre);

    boolean deleteById(int id);

    Optional<Genre> findById(int id);

    Collection<Genre> findAll();
}
