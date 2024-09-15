package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.service.FilmService;

import javax.servlet.http.HttpSession;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilmControllerTest {
    private FilmController filmController;
    private FilmService filmService;
    private HttpSession session;

    @BeforeEach
    public void initServices() {
        filmService = mock(FilmService.class);
        filmController = new FilmController(filmService);
        session = new MockHttpSession();
    }

    @Test
    public void whenRequestFilmListPageThenGetPageWithFilms() {
        var film1 = new FilmDto(0, "film1", "description1", 2024,  12, 120, "comedy");
        var film2 = new FilmDto(1, "film2", "description2", 2023, 14, 123, "triller");
        var expectedFilms = List.of(film1, film2);
        when(filmService.findAll()).thenReturn(expectedFilms);

        var model = new ConcurrentModel();
        var view = filmController.getAll(model, session);
        var actualFilms = model.getAttribute("filmsdto");

        assertThat(view).isEqualTo("films/list");
        assertThat(actualFilms).isEqualTo(expectedFilms);
 }
}
