package ru.job4j.cinema.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmService;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/films")
@ThreadSafe
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public String getAll(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setFullName("Гость");
        }
        model.addAttribute("user", user);
        model.addAttribute("films", filmService.findAll());
        return "films/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id, HttpSession session) {
        var filmOptional = filmService.findById(id);
        if (filmOptional.isEmpty()) {
            model.addAttribute("message", "Фильм с указанным идентификатором не найден.");
            return "templates/errors/404";
        }
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setFullName("Гость");
        }
        model.addAttribute("user", user);
        model.addAttribute("film", filmOptional.get());
        return "films/one";
    }
}
