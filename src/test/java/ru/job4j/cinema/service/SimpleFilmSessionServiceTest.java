package ru.job4j.cinema.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.repository.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleFilmSessionServiceTest {

    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oGenreRepository sql2oGenreRepository;

    private static Sql2oHallRepository sql2oHallRepository;

    private static SimpleFilmSessionService simpleFilmSessionService;

    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;


    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = SimpleFilmSessionServiceTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);

        simpleFilmSessionService = new SimpleFilmSessionService(sql2oFilmRepository, sql2oFilmSessionRepository, sql2oHallRepository);

    }

    @AfterEach
    public void clear() throws IOException {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        for (var filmSession : filmSessions) {
            sql2oFilmSessionRepository.deleteById(filmSession.getId());

        }
        var films = sql2oFilmRepository.findAll();
        for (var film : films) {
            sql2oFilmRepository.deleteById(film.getId());
        }
        var files = sql2oFileRepository.findAll();
        for (var file : files) {
            sql2oFileRepository.deleteById(file.getId());
        }
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
        var halls = sql2oHallRepository.findAll();
        for (var hall : halls) {
            sql2oHallRepository.deleteById(hall.getId());
        }
    }


    @Test
    public void whenSaveThenGetSame() throws IOException {
        var file = sql2oFileRepository.save(new File("test2.jpg", "test2"));
        var genre = new Genre(1, "comedy1");
        var savedGenre = sql2oGenreRepository.save(genre);
        var film = new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId());
        var savedFilm = sql2oFilmRepository.save(film);
        var hall = new Hall(1, "hall", 5, 5, "hall desc");
        var savedHall = sql2oHallRepository.save(hall);
        var filmSession = new FilmSession(1, savedFilm.getId(), savedHall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200);
        var savedFilmSession = simpleFilmSessionService.save(filmSession);
        var filmSessionDto = simpleFilmSessionService.findById(savedFilmSession.getId()).get();
        var expectedFilmSessionDto = new FilmSessionDto(savedFilmSession.getId(), file.getId(), savedFilm.getName(), savedFilm.getDescription(), savedFilmSession.getStartTime(), savedFilmSession.getEndTime(), savedFilmSession.getPrice(), savedHall.getName(), savedHall.getRowCount(), savedHall.getPlaceCount());

        assertThat(filmSessionDto).usingRecursiveComparison().isEqualTo(expectedFilmSessionDto);
    }

    @Test
    public void whenSaveSeveralThenGetAll() throws IOException {
        var file = sql2oFileRepository.save(new File("test3.jpg", "test3"));
        var genre = new Genre(1, "comedy1");
        var savedGenre = sql2oGenreRepository.save(genre);
        var film = new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId());
        var savedFilm = sql2oFilmRepository.save(film);
        var hall = new Hall(1, "hall", 5, 5, "hall desc");
        var savedHall = sql2oHallRepository.save(hall);
        var filmSession1 = new FilmSession(1, savedFilm.getId(), savedHall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200);
        var filmSession2 = new FilmSession(2, savedFilm.getId(), savedHall.getId(), LocalDateTime.of(2022, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 250);
        var filmSession3 = new FilmSession(3, savedFilm.getId(), savedHall.getId(), LocalDateTime.of(2021, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 300);
        var savedFilmSession1 = simpleFilmSessionService.save(filmSession1);
        var savedFilmSession2 = simpleFilmSessionService.save(filmSession2);
        var savedFilmSession3 = simpleFilmSessionService.save(filmSession3);

        var expectedFilmSessionDto1 = new FilmSessionDto(savedFilm, savedFilmSession1, savedHall);
        var expectedFilmSessionDto2 = new FilmSessionDto(savedFilm, savedFilmSession2, savedHall);
        var expectedFilmSessionDto3 = new FilmSessionDto(savedFilm, savedFilmSession3, savedHall);

        var result = simpleFilmSessionService.findAll();
        var expected = new ArrayList<>();
        expected.add(expectedFilmSessionDto1);
        expected.add(expectedFilmSessionDto2);
        expected.add(expectedFilmSessionDto3);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(simpleFilmSessionService.findAll()).isEqualTo(emptyList());
        assertThat(simpleFilmSessionService.findById(0)).isEqualTo(empty());
    }


    @Test
    public void whenDeleteThenGetEmptyOptional() throws IOException {
        var file = sql2oFileRepository.save(new File("test4.jpg", "test4"));
        var genre = new Genre(1, "comedy1");
        var savedGenre = sql2oGenreRepository.save(genre);
        var film = new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId());
        var savedFilm = sql2oFilmRepository.save(film);
        var hall = new Hall(1, "hall", 5, 5, "hall desc");
        var savedHall = sql2oHallRepository.save(hall);
        var filmSession = new FilmSession(1, savedFilm.getId(), savedHall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200);
        var savedFilmSession = simpleFilmSessionService.save(filmSession);

        var isDeleted = simpleFilmSessionService.deleteById(savedFilmSession.getId());
        var notFoundFilmSession = simpleFilmSessionService.findById(savedFilmSession.getId());

        assertThat(isDeleted).isTrue();
        assertThat(notFoundFilmSession).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(simpleFilmSessionService.deleteById(0)).isFalse();
    }
}
