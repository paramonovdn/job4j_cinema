package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Film save(Film film);

    boolean deleteById(int id);

    boolean update(Film film);

    Optional<Film> findById(int id);

    Collection<Film> findAll();
}
