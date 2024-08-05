package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.service.FilmService;
import ru.job4j.cinema.service.FilmSessionService;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.TicketService;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/tickets")
@ThreadSafe
public class TicketController {

    private final TicketService ticketService;
    private final FilmSessionRepository filmSessionRepository;

    private final FilmRepository filmRepository;
    private final HallService hallService;

    public TicketController(TicketService ticketService, FilmSessionRepository filmSessionRepository, FilmRepository filmRepository,
                            HallService hallService) {
        this.ticketService = ticketService;
        this.filmSessionRepository = filmSessionRepository;
        this.filmRepository = filmRepository;
        this.hallService = hallService;
    }

    @PostMapping("/buy")
    public String buyTicket(@ModelAttribute Ticket ticket, Model model, HttpSession session) {
        try {
            var user = (User) session.getAttribute("user");
            if (user == null) {
                user = new User();
                user.setFullName("Гость");
                model.addAttribute("error", "Для покупки билета необходимо войти на сайт.");
                return "users/login";
            }
            model.addAttribute("user", user);
            var ticketOptional = ticketService.findTicketByRowAndPlace(ticket.getSessionId(), ticket.getRowNumber(),
                    ticket.getPlaceNumber());
            if (ticketOptional.isPresent()) {
                model.addAttribute("message", "Не удалось приобрести билет на заданное место. "
                        + "Вероятно оно уже занято. Перейдите на страницу бронирования билетов и попробуйте снова.");
                return "errors/404";
            }
            ticketService.save(ticket);
            model.addAttribute("message", "Вы успешно приобрели билет на- "
                    + ticket.getRowNumber() + " ряд, " + ticket.getPlaceNumber() + " место.");
            return "errors/200";
        } catch (Exception exception) {
            model.addAttribute("message", exception.getMessage());
            return "errors/404";
        }
    }

    @GetMapping("/{buy}")
    public String getBuyPage(Model model, @PathVariable int id) {
        var filmSessionOptional = filmSessionRepository.findById(id);
        if (filmSessionOptional.isEmpty()) {
            model.addAttribute("message", "Киносеанс с данным идентификатором не найден.");
            return "errors/404";
        }
        model.addAttribute("filmsessiondto", new FilmSessionDto(filmRepository.findById(id).get(), filmSessionOptional.get(),
                hallService.findById(id).get()));
        return "tickets/buy";
    }

}
