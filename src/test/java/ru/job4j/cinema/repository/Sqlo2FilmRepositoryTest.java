package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import java.util.List;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sqlo2FilmRepositoryTest {


    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oGenreRepository sql2oGenreRepository;

    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sqlo2FilmRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
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


        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clear() {
        var films = sql2oFilmRepository.findAll();
        for (var film : films) {
            sql2oFilmRepository.deleteById(film.getId());
        }
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        var savedCandidate = sql2oFilmRepository.findById(film.get().getId()).get();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(film.get());
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);

        var film1 = sql2oFilmRepository.save(new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        var film2 = sql2oFilmRepository.save(new Film(2, "film2", "description2", 2023, genre.getId(), 14, 96, file.getId()));
        var film3 = sql2oFilmRepository.save(new Film(3, "film3", "description3", 2022, genre.getId(), 16, 99, file.getId()));

        var result = sql2oFilmRepository.findAll();
        assertThat(result).isEqualTo(List.of(film1.get(), film2.get(), film3.get()));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFilmRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oFilmRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        var isDeleted = sql2oFilmRepository.deleteById(film.get().getId());
        var savedFilm = sql2oFilmRepository.findById(film.get().getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedFilm).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oFilmRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var genre = new Genre(1, "comedy");
        sql2oGenreRepository.save(genre);
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()));
        var updatedFilm = new Film(film.get().getId(), "updated film1", "updated description1", 2024, genre.getId(), 12, 120, file.getId());
        var isUpdated = sql2oFilmRepository.update(updatedFilm);
        var savedFilm = sql2oFilmRepository.findById(updatedFilm.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedFilm).usingRecursiveComparison().isEqualTo(updatedFilm);
    }

    @Test
    public void whenUpdateUnExistingCandidateThenGetFalse() {
        var film = new Film(0, "film1", "description1", 2024, 1, 12, 120, file.getId());
        var isUpdated = sql2oFilmRepository.update(film);
        assertThat(isUpdated).isFalse();
    }

}
