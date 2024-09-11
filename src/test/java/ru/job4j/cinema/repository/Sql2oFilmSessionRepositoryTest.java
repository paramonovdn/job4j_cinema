package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oFilmSessionRepositoryTest {

    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oHallRepository sql2oHallRepository;
    private static Sql2oGenreRepository sql2oGenreRepository;

    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFilmRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);



        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clear() {
        var filmSessions = sql2oFilmSessionRepository.findAll();
        for (var filmSession : filmSessions) {
            sql2oFilmSessionRepository.deleteById(filmSession.getId());
        }
        var films = sql2oFilmRepository.findAll();
        for (var film : films) {
            sql2oFilmRepository.deleteById(film.getId());
        }
        var halls = sql2oHallRepository.findAll();
        for (var hall : halls) {
            sql2oHallRepository.deleteById(hall.getId());
        }
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var hall = new Hall(1, "hall1", 5, 5, "small hall");
        sql2oHallRepository.save(hall);
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var savedFilmSession = sql2oFilmSessionRepository.findById(filmSession.getId()).get();
        assertThat(savedFilmSession).usingRecursiveComparison().isEqualTo(filmSession);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var hall = new Hall(1, "hall1", 5, 5, "small hall");
        sql2oHallRepository.save(hall);
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        sql2oFilmRepository.save(film);
        var filmSession1 = sql2oFilmSessionRepository.save(new FilmSession(1, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var filmSession2 = sql2oFilmSessionRepository.save(new FilmSession(2, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 250));
        var filmSession3 = sql2oFilmSessionRepository.save(new FilmSession(3, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 300));

        var result = sql2oFilmSessionRepository.findAll();
        assertThat(result).isEqualTo(List.of(filmSession1, filmSession2, filmSession3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFilmSessionRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oFilmSessionRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var hall = new Hall(1, "hall1", 5, 5, "small hall");
        sql2oHallRepository.save(hall);
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        sql2oFilmRepository.save(film);
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(1, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));

        var isDeleted = sql2oFilmSessionRepository.deleteById(filmSession.getId());
        var savedCandidate = sql2oFilmRepository.findById(filmSession.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedCandidate).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFilmSessionRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var hall = new Hall(1, "hall1", 5, 5, "small hall");
        sql2oHallRepository.save(hall);
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        sql2oFilmRepository.save(film);
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(1, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var updatedFilmSession = new FilmSession(filmSession.getId(), film.getId(), hall.getId(), LocalDateTime.of(2022, Month.SEPTEMBER, 9, 18, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), 250);
        var isUpdated = sql2oFilmSessionRepository.update(updatedFilmSession);
        var savedFilm = sql2oFilmSessionRepository.findById(updatedFilmSession.getId()).get();

        assertThat(isUpdated).isTrue();
        assertThat(savedFilm).usingRecursiveComparison().isEqualTo(updatedFilmSession);
    }

    @Test
    public void whenUpdateUnExistingFilmSessionThenGetFalse() {
        var hall = new Hall(1, "hall1", 5, 5, "small hall");
        sql2oHallRepository.save(hall);
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        sql2oFilmRepository.save(film);
        var filmSession = new FilmSession(1, film.getId(), hall.getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200);
        var isUpdated = sql2oFilmSessionRepository.update(filmSession);
        assertThat(isUpdated).isFalse();
    }

}
