package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.model.Hall;

import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oHallRepositoryTest {


    private static Sql2oHallRepository sql2oHallRepository;


    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oHallRepository = new Sql2oHallRepository(sql2o);

    }

    @AfterEach
    public void clearFiles() {
        var halls = sql2oHallRepository.findAll();
        for (var hall : halls) {
            sql2oHallRepository.deleteById(hall.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var savedHall = new Hall(0, "test hall", 5, 5, "desvription");
        sql2oHallRepository.save(savedHall);
        var expectedHall = sql2oHallRepository.findById(savedHall.getId()).get();

        assertThat(savedHall).usingRecursiveComparison().isEqualTo(expectedHall);
    }

    @Test
    public void whenSaveThenGetSeveralSame() {
        var savedHall1 = new Hall(1, "hall1", 5, 5, "desc1");
        var savedHall2 = new Hall(2, "hall2", 6, 6, "desc2");
        var savedHall3 = new Hall(3, "hall3", 7, 7, "desc3");
        sql2oHallRepository.save(savedHall1);
        sql2oHallRepository.save(savedHall2);
        sql2oHallRepository.save(savedHall3);
        var extectedHall1 = sql2oHallRepository.findById(savedHall1.getId()).get();
        var extectedHall2 = sql2oHallRepository.findById(savedHall2.getId()).get();
        var extectedHall3 = sql2oHallRepository.findById(savedHall3.getId()).get();

        assertThat(savedHall1).usingRecursiveComparison().isEqualTo(extectedHall1);
        assertThat(savedHall2).usingRecursiveComparison().isEqualTo(extectedHall2);
        assertThat(savedHall3).usingRecursiveComparison().isEqualTo(extectedHall3);
    }

    @Test
    public void whenDeleteThenGetSeveralSame() {
        var savedHall1 = new Hall(1, "hall1", 5, 5, "desc1");
        var savedHall2 = new Hall(2, "hall2", 6, 6, "desc2");
        var savedHall3 = new Hall(3, "hall3", 7, 7, "desc3");

        sql2oHallRepository.save(savedHall1);
        sql2oHallRepository.save(savedHall2);
        sql2oHallRepository.save(savedHall3);

        assertThat(sql2oHallRepository.findAll()).isEqualTo(List.of(savedHall1, savedHall2, savedHall3));

        assertThat(sql2oHallRepository.findAll().size()).isEqualTo(3);
        sql2oHallRepository.deleteById(savedHall1.getId());
        assertThat(sql2oHallRepository.findAll().size()).isEqualTo(2);
        sql2oHallRepository.deleteById(savedHall2.getId());
        assertThat(sql2oHallRepository.findAll().size()).isEqualTo(1);
        sql2oHallRepository.deleteById(savedHall3.getId());
        assertThat(sql2oHallRepository.findAll().size()).isEqualTo(0);

        assertThat(sql2oHallRepository.findAll()).isEqualTo(List.of());
    }
    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oHallRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oHallRepository.findById(0)).isEqualTo(empty());
    }
}
