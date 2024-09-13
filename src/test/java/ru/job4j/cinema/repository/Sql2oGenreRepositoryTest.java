package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;

import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oGenreRepositoryTest {

    private static Sql2oGenreRepository sql2oGenreRepository;


    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oGenreRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);

    }

    @AfterEach
    public void clearFiles() {
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var savedGenre = new Genre(0, "genre");
        sql2oGenreRepository.save(savedGenre);
        var expectedFile = sql2oGenreRepository.findById(savedGenre.getId()).get();

        assertThat(savedGenre).usingRecursiveComparison().isEqualTo(expectedFile);
    }

    @Test
    public void whenSaveThenGetSeveralSame() {
        var savedGenre1 = new Genre(1, "genre1");
        var savedGenre2 = new Genre(2, "genre2");
        var savedGenre3 = new Genre(3, "genre3");
        sql2oGenreRepository.save(savedGenre1);
        sql2oGenreRepository.save(savedGenre2);
        sql2oGenreRepository.save(savedGenre3);
        var extectedGenre1 = sql2oGenreRepository.findById(savedGenre1.getId()).get();
        var extectedGenre2 = sql2oGenreRepository.findById(savedGenre2.getId()).get();
        var extectedGenre3 = sql2oGenreRepository.findById(savedGenre3.getId()).get();

        assertThat(savedGenre1).usingRecursiveComparison().isEqualTo(extectedGenre1);
        assertThat(savedGenre2).usingRecursiveComparison().isEqualTo(extectedGenre2);
        assertThat(savedGenre3).usingRecursiveComparison().isEqualTo(extectedGenre3);
    }

    @Test
    public void whenDeleteThenGetSeveralSame() {
        var savedGenre1 = new Genre(1, "genre1");
        var savedGenre2 = new Genre(2, "genre2");
        var savedGenre3 = new Genre(3, "genre3");
        sql2oGenreRepository.save(savedGenre1);
        sql2oGenreRepository.save(savedGenre2);
        sql2oGenreRepository.save(savedGenre3);

        assertThat(sql2oGenreRepository.findAll()).isEqualTo(List.of(savedGenre1, savedGenre2, savedGenre3));

        assertThat(sql2oGenreRepository.findAll().size()).isEqualTo(3);
        sql2oGenreRepository.deleteById(savedGenre1.getId());
        assertThat(sql2oGenreRepository.findAll().size()).isEqualTo(2);
        sql2oGenreRepository.deleteById(savedGenre2.getId());
        assertThat(sql2oGenreRepository.findAll().size()).isEqualTo(1);
        sql2oGenreRepository.deleteById(savedGenre3.getId());
        assertThat(sql2oGenreRepository.findAll().size()).isEqualTo(0);

        assertThat(sql2oGenreRepository.findAll()).isEqualTo(List.of());
    }




    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oGenreRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oGenreRepository.findById(0)).isEqualTo(empty());
    }
}
