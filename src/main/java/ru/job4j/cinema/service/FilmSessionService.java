package ru.job4j.cinema.service;

import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;

import java.util.Collection;
import java.util.Optional;

public interface FilmSessionService {

    FilmSession save(FilmSession filmSession);

    boolean deleteById(int id);

    boolean update(FilmSession filmSession);

    Optional<FilmSessionDto> findById(int id);

    Collection<FilmSessionDto> findAll();
}
