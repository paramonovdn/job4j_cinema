package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.TicketService;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/tickets")
@ThreadSafe
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
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

}
