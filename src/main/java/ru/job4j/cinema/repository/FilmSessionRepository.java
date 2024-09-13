package ru.job4j.cinema.repository;

import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionRepository {

    FilmSession save(FilmSession filmSession);

    boolean deleteById(int id);

    boolean update(FilmSession filmSession);

    Optional<FilmSession> findById(int id);

    Collection<FilmSession> findAll();
}
