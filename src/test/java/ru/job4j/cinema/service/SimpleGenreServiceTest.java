package ru.job4j.cinema.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.Sql2oGenreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleGenreServiceTest {

    private static SimpleGenreService simpleGenreService;
    private static Sql2oGenreRepository sql2oGenreRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = SimpleGenreServiceTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        simpleGenreService = new SimpleGenreService(sql2oGenreRepository);

    }

    @AfterEach
    public void clear() {
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() throws IOException {
        var savedGenre = simpleGenreService.save(new Genre(1, "comedy1"));
        var findedGenre = simpleGenreService.findById(savedGenre.getId()).get();

        assertThat(savedGenre).usingRecursiveComparison().isEqualTo(findedGenre);
    }

    @Test
    public void whenSaveSeveralThenGetAll() throws IOException {
        var savedGenre1 = simpleGenreService.save(new Genre(1, "comedy"));
        var savedGenre2 = simpleGenreService.save(new Genre(2, "triller"));
        var savedGenre3 = simpleGenreService.save(new Genre(3, "horror"));
        var findedGenre1 = simpleGenreService.findById(savedGenre1.getId()).get();
        var findedGenre2 = simpleGenreService.findById(savedGenre2.getId()).get();
        var findedGenre3 = simpleGenreService.findById(savedGenre3.getId()).get();

        var result = simpleGenreService.findAll();


        var expected = new ArrayList<>();
        expected.add(findedGenre1);
        expected.add(findedGenre2);
        expected.add(findedGenre3);

        var containts = result.containsAll(result);

        assertThat(containts).isEqualTo(true);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(simpleGenreService.findAll()).isEqualTo(emptyList());
        assertThat(simpleGenreService.findById(0)).isEqualTo(empty());
    }


    @Test
    public void whenDeleteThenGetEmptyOptional() throws IOException {
        var savedGenre = simpleGenreService.save(new Genre(1, "comedy1"));
        var isDeleted = simpleGenreService.deleteById(savedGenre.getId());
        var isFinded = simpleGenreService.findById(savedGenre.getId());

        assertThat(isDeleted).isTrue();
        assertThat(isFinded).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(simpleGenreService.deleteById(0)).isFalse();
    }

}
