package ru.job4j.cinema.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionService;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/filmsessions")
@ThreadSafe
public class FilmSessionController {
    private final FilmSessionService filmSessionService;

    public FilmSessionController(FilmSessionService filmSessionService) {
        this.filmSessionService = filmSessionService;
    }

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setFullName("Гость");
        }
        model.addAttribute("user", user);
        model.addAttribute("filmsessionsdto", filmSessionService.findAll());
        return "filmsessions/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id, HttpSession session) {
        var filmSessionOptional = filmSessionService.findById(id);
        if (filmSessionOptional.isEmpty()) {
            model.addAttribute("message", "Киносеанс с указанным идентификатором не найден.");
            return "templates/errors/404";
        }
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setFullName("Гость");
        }
        model.addAttribute("user", user);
        model.addAttribute("filmsession", filmSessionOptional.get());
        return "filmsessions/one";
    }
}
