package ru.job4j.cinema.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.Sql2oUserRepository;
import ru.job4j.cinema.repository.Sql2oUserRepositoryTest;

import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleUserServiceTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static SimpleUserService simpleUserService;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
        simpleUserService = new SimpleUserService(sql2oUserRepository);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveAndFindByEmailAndPassword() {
        var user = simpleUserService.save(new User(1, "Jonson John", "ivanov@mail.ru", "jonson123")).get();
        var savedUser = simpleUserService.findByEmailAndPassword("ivanov@mail.ru", "jonson123").get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }


    @Test
    public void whenSaveAndFindByEmailAndPasswordMoreThenOne() {
        var user1 = simpleUserService.save(new User(2,  "Pedro Pascal", "pascal@mail.ru", "123pascal")).get();
        var user2 = simpleUserService.save(new User(3,  "Petrov Petr", "456@mail.ru", "petro")).get();
        var savedUser1 = simpleUserService.findByEmailAndPassword("pascal@mail.ru", "123pascal").get();
        var savedUser2 = simpleUserService.findByEmailAndPassword("456@mail.ru", "petro").get();
        assertThat(savedUser1).usingRecursiveComparison().isEqualTo(user1);
        assertThat(savedUser2).usingRecursiveComparison().isEqualTo(user2);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(simpleUserService.findByEmailAndPassword("nothing2@mail.ru", "nothing"))
                .isEqualTo(empty());
    }

    @Test
    public void whenSaveExistEmail() {
        simpleUserService.save(new User(6, "Serj Tankjan", "exist@mail.ru", "123")).get();
        assertThat(simpleUserService.save(new User(6,  "Serj Tankjan", "exist@mail.ru", "123")))
                .isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetSame() {
        var user1 = simpleUserService.save(new User(2,  "Pedro Pascal", "pascal@mail.ru", "123pascal")).get();
        var user2 = simpleUserService.save(new User(3,  "Petrov Petr", "456@mail.ru", "petro")).get();
        simpleUserService.deleteById(user1.getId());
        assertThat(simpleUserService.findAll().size()).isEqualTo(1);
        simpleUserService.deleteById(user2.getId());
        assertThat(simpleUserService.findAll().size()).isEqualTo(0);
    }
}
