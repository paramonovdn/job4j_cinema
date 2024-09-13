package ru.job4j.cinema.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.repository.Sql2oHallRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleHallServiceTest {
    private static SimpleHallService simpleHallService;
    private static Sql2oHallRepository sql2oHallRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = SimpleHallServiceTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        simpleHallService = new SimpleHallService(sql2oHallRepository);
    }

    @AfterEach
    public void clear() {
        var halls = sql2oHallRepository.findAll();
        for (var hall : halls) {
            sql2oHallRepository.deleteById(hall.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() throws IOException {
        var savedHall = simpleHallService.save(new Hall(1, "hall", 5, 5, "desc"));
        var findedHall = simpleHallService.findById(savedHall.getId()).get();

        assertThat(savedHall).usingRecursiveComparison().isEqualTo(findedHall);
    }

    @Test
    public void whenSaveSeveralThenGetAll() throws IOException {
        var savedGenre1 = simpleHallService.save(new Hall(1, "hall1", 5, 5, "desc1"));
        var savedGenre2 = simpleHallService.save(new Hall(2, "hall2", 7, 7, "desc2"));
        var savedGenre3 = simpleHallService.save(new Hall(3, "hall3", 10, 10, "desc3"));
        var findedHall1 = simpleHallService.findById(savedGenre1.getId()).get();
        var findedHall2 = simpleHallService.findById(savedGenre2.getId()).get();
        var findedHall3 = simpleHallService.findById(savedGenre3.getId()).get();

        var result = simpleHallService.findAll();


        var expected = new ArrayList<>();
        expected.add(findedHall1);
        expected.add(findedHall2);
        expected.add(findedHall3);

        var containts = result.containsAll(result);

        assertThat(containts).isEqualTo(true);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(simpleHallService.findAll()).isEqualTo(emptyList());
        assertThat(simpleHallService.findById(0)).isEqualTo(empty());
    }


    @Test
    public void whenDeleteThenGetEmptyOptional() throws IOException {
        var savedGenre = simpleHallService.save(new Hall(1, "hall", 5, 5, "desc"));
        var isDeleted = simpleHallService.deleteById(savedGenre.getId());
        var isFinded = simpleHallService.findById(savedGenre.getId());

        assertThat(isDeleted).isTrue();
        assertThat(isFinded).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(simpleHallService.deleteById(0)).isFalse();
    }

}
