package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.service.FilmSessionService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilmSessionsControllerTest {

    private FilmSessionController filmSessionController;
    private FilmSessionService filmSessionService;
    private HttpSession session;

    @BeforeEach
    public void initServices() {
        filmSessionService = mock(FilmSessionService.class);
        filmSessionController = new FilmSessionController(filmSessionService);
        session = new MockHttpSession();
    }

    @Test
    public void whenRequestFilmSessionListPageThenGetPageWithFilmSessions() {
        var filmSession1 = new FilmSessionDto(1, 1, "film1", "desc1", LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200, "hall1", 5, 5);
        var filmSession2 = new FilmSessionDto(2, 2, "film2", "desc1", LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200, "hall2", 10, 10);
        var expectedFilmSessions = List.of(filmSession1, filmSession2);
        when(filmSessionService.findAll()).thenReturn(expectedFilmSessions);

        var model = new ConcurrentModel();
        var view = filmSessionController.getAll(model, session);
        var actualFilms = model.getAttribute("filmsessionsdto");

        assertThat(view).isEqualTo("filmsessions/list");
        assertThat(actualFilms).isEqualTo(expectedFilmSessions);
    }

    @Test
    public void whenRequestVacancyPageThenGetPageWithVacancy() {
        var filmSession = new FilmSessionDto(1, 1, "film1", "desc1", LocalDateTime.of(2024, Month.SEPTEMBER, 9, 20, 00, 00), LocalDateTime.of(2024, Month.SEPTEMBER, 9, 22, 00, 00), 200, "hall1", 5, 5);
        var expectedFilmSession = Optional.ofNullable(filmSession);
        when(filmSessionService.findById(1)).thenReturn(expectedFilmSession);

        var model = new ConcurrentModel();
        var view = filmSessionController.getById(model, 1);
        var actualFilmSession = model.getAttribute("filmsessiondto");

        assertThat(view).isEqualTo("filmsessions/one");
        assertThat(actualFilmSession).isEqualTo(expectedFilmSession.get());
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage2() {
        var expectedException = new RuntimeException("Киносеанс с указанным идентификатором не найден.");

        var model = new ConcurrentModel();
        var view = filmSessionController.getById(model, 0);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/409");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }


}
