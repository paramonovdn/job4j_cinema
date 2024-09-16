package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TicketControllerTest {
    private TicketController ticketController;
    private TicketService ticketService;

    private HttpSession session;

    @BeforeEach
    public void initServices() {
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(ticketService);
        session = new MockHttpSession();
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var ticket = new  Ticket(1, 1, 1, 1, 1);
        var expectedException = new RuntimeException("Для покупки билета необходимо войти на сайт.");

        var model = new ConcurrentModel();
        var view = ticketController.buyTicket(ticket, model, session);
        var actualExceptionMessage = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage2() {
        var ticket = new  Ticket(1, 1, 1, 1, 1);
        session.setAttribute("user", new User(1, "name", "email@mail.ru", "pass"));
        var expectedException = new RuntimeException("Не удалось приобрести билет на выбранное место. "
                + "Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
        when(ticketService.findTicketByRowAndPlace(ticket.getSessionId(), ticket.getRowNumber(), ticket.getPlaceNumber())).
                thenReturn(Optional.ofNullable(ticket));
        var model = new ConcurrentModel();
        var view = ticketController.buyTicket(ticket, model, session);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/409");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage3() {
        var ticket = new  Ticket(1, 1, 1, 1, 1);
        session.setAttribute("user", new User(1, "name", "email@mail.ru", "pass"));
        var expectedException = new RuntimeException("Вы успешно приобрели билет на- "
                + ticket.getRowNumber() + " ряд, " + ticket.getPlaceNumber() + " место.");
        when(ticketService.findTicketByRowAndPlace(ticket.getSessionId(), ticket.getRowNumber(), ticket.getPlaceNumber())).
                thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = ticketController.buyTicket(ticket, model, session);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/200");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }


}
