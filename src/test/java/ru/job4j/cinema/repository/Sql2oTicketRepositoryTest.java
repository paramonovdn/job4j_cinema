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

public class Sql2oTicketRepositoryTest {
    private static Sql2oTicketRepository sql2oTicketRepository;
    private static Sql2oFilmSessionRepository sql2oFilmSessionRepository;
    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oHallRepository sql2oHallRepository;
    private static Sql2oGenreRepository sql2oGenreRepository;

    private static Sql2oUserRepository sql2oUserRepository;

    private static File file;



    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oTicketRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oTicketRepository = new Sql2oTicketRepository(sql2o);
        sql2oFilmSessionRepository = new Sql2oFilmSessionRepository(sql2o);
        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oHallRepository = new Sql2oHallRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearFiles() {
        var tickets = sql2oTicketRepository.findAll();
        for (var ticket : tickets) {
            sql2oTicketRepository.deleteById(ticket.getId());
        }
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
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var hall =  sql2oHallRepository.save(new Hall(1, "hall1", 5, 5, "small hall"));
        var genre = sql2oGenreRepository.save(new Genre(1, "comedy"));
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.get().getId(), 12, 120, file.getId()));
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.get().getId(), hall.get().getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var user = sql2oUserRepository.save(new User(0, "name", "email@mail.ru", "pass")).get();
        var savedTicket = sql2oTicketRepository.save(new Ticket(0, filmSession.get().getId(), 5, 5, user.getId())).get();
        var expectedTicket = sql2oTicketRepository.findById(savedTicket.getId()).get();

        assertThat(savedTicket).usingRecursiveComparison().isEqualTo(expectedTicket);
    }

    @Test
    public void whenSaveThenGetSeveralSame() {
        var hall =  sql2oHallRepository.save(new Hall(1, "hall1", 5, 5, "small hall"));
        var genre = sql2oGenreRepository.save(new Genre(1, "comedy"));
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.get().getId(), 12, 120, file.getId()));
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.get().getId(), hall.get().getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var user = sql2oUserRepository.save(new User(0, "name", "email@mail.ru", "pass")).get();
        var savedTicket1 = sql2oTicketRepository.save(new Ticket(1, filmSession.get().getId(), 5, 5, user.getId())).get();
        var savedTicket2 = sql2oTicketRepository.save(new Ticket(2, filmSession.get().getId(), 6, 6, user.getId())).get();
        var savedTicket3 = sql2oTicketRepository.save(new Ticket(3, filmSession.get().getId(), 7, 7, user.getId())).get();

        var extectedTicket1 = sql2oTicketRepository.findById(savedTicket1.getId()).get();
        var extectedTicket2 = sql2oTicketRepository.findById(savedTicket2.getId()).get();
        var extectedTicket3 = sql2oTicketRepository.findById(savedTicket3.getId()).get();

        assertThat(savedTicket1).usingRecursiveComparison().isEqualTo(extectedTicket1);
        assertThat(savedTicket2).usingRecursiveComparison().isEqualTo(extectedTicket2);
        assertThat(savedTicket3).usingRecursiveComparison().isEqualTo(extectedTicket3);
    }

    @Test
    public void whenDeleteThenGetSeveralSame() {
        var hall =  sql2oHallRepository.save(new Hall(1, "hall1", 5, 5, "small hall"));
        var genre = sql2oGenreRepository.save(new Genre(1, "comedy"));
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.get().getId(), 12, 120, file.getId()));
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.get().getId(), hall.get().getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var user = sql2oUserRepository.save(new User(0, "name", "email@mail.ru", "pass")).get();
        var savedTicket1 = sql2oTicketRepository.save(new Ticket(1, filmSession.get().getId(), 5, 5, user.getId())).get();
        var savedTicket2 = sql2oTicketRepository.save(new Ticket(2, filmSession.get().getId(), 6, 6, user.getId())).get();
        var savedTicket3 = sql2oTicketRepository.save(new Ticket(3, filmSession.get().getId(), 7, 7, user.getId())).get();

        assertThat(sql2oTicketRepository.findAll()).isEqualTo(List.of(savedTicket1, savedTicket2, savedTicket3));

        assertThat(sql2oTicketRepository.findAll().size()).isEqualTo(3);
        sql2oTicketRepository.deleteById(savedTicket1.getId());
        assertThat(sql2oTicketRepository.findAll().size()).isEqualTo(2);
        sql2oTicketRepository.deleteById(savedTicket2.getId());
        assertThat(sql2oTicketRepository.findAll().size()).isEqualTo(1);
        sql2oTicketRepository.deleteById(savedTicket3.getId());
        assertThat(sql2oTicketRepository.findAll().size()).isEqualTo(0);

        assertThat(sql2oTicketRepository.findAll()).isEqualTo(List.of());
    }
    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oTicketRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oTicketRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public  void  whenSearchTicketThenGetSame() {
        var hall =  sql2oHallRepository.save(new Hall(1, "hall1", 5, 5, "small hall"));
        var genre = sql2oGenreRepository.save(new Genre(1, "comedy"));
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.get().getId(), 12, 120, file.getId()));
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.get().getId(), hall.get().getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var user = sql2oUserRepository.save(new User(0, "name", "email@mail.ru", "pass")).get();
        var savedTicket = sql2oTicketRepository.save(new Ticket(0, filmSession.get().getId(), 5, 5, user.getId())).get();

        var findedTicket = sql2oTicketRepository.findTicketByRowAndPlace(filmSession.get().getId(), savedTicket.getRowNumber(), savedTicket.getPlaceNumber()).get();

        assertThat(savedTicket).usingRecursiveComparison().isEqualTo(findedTicket);
    }

    @Test
    public  void  whenSearchTicketThenGetEmptyOpyional() {
        var findedTicket = sql2oTicketRepository.findTicketByRowAndPlace(0, 0, 0);

        assertThat(findedTicket).usingRecursiveComparison().isEqualTo(findedTicket);
    }

    @Test
    public  void  whenSaveAndDeleteTicketThenGetEmpty() {
        var hall =  sql2oHallRepository.save(new Hall(1, "hall1", 5, 5, "small hall"));
        var genre = sql2oGenreRepository.save(new Genre(1, "comedy"));
        var film = sql2oFilmRepository.save(new Film(0, "film1", "description1", 2024, genre.get().getId(), 12, 120, file.getId()));
        var filmSession = sql2oFilmSessionRepository.save(new FilmSession(0, film.get().getId(), hall.get().getId(), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200));
        var user = sql2oUserRepository.save(new User(0, "name", "email@mail.ru", "pass")).get();
        var savedTicket = sql2oTicketRepository.save(new Ticket(0, filmSession.get().getId(), 5, 5, user.getId())).get();
        sql2oTicketRepository.deleteById(savedTicket.getId());
        var findedTicket = sql2oTicketRepository.findTicketByRowAndPlace(filmSession.get().getId(), savedTicket.getRowNumber(), savedTicket.getPlaceNumber());

        assertThat(findedTicket).isEqualTo(empty());
    }
}
