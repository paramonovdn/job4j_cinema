package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FilmSessionDto;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.FilmSessionRepository;
import ru.job4j.cinema.repository.HallRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
@Service
@ThreadSafe
public class SimpleFilmSessionService implements FilmSessionService {
    private final FilmRepository filmRepository;
    private final FilmSessionRepository filmSessionRepository;
    private final HallRepository hallRepository;

    public SimpleFilmSessionService(FilmRepository filmRepository, FilmSessionRepository filmSessionRepository, HallRepository hallRepository) {
        this.filmRepository = filmRepository;
        this.filmSessionRepository = filmSessionRepository;
        this.hallRepository = hallRepository;
    }

    @Override
    public FilmSession save(FilmSession filmSession) {
        return filmSessionRepository.save(filmSession);
    }

    @Override
    public boolean deleteById(int id) {
        return filmSessionRepository.deleteById(id);
    }

    @Override
    public Optional<FilmSessionDto> findById(int id) {
        var filmSession = filmSessionRepository.findById(id);
        if (filmSession.isEmpty()) {
            return Optional.empty();
        }
        var film = filmRepository.findById(filmSession.get().getFilmId()).get();
        var hall = hallRepository.findById(filmSession.get().getHallsId()).get();
        return Optional.ofNullable(new FilmSessionDto(film, filmSession.get(), hall));
    }

    @Override
    public Collection<FilmSessionDto> findAll() {
        var filmSessions = filmSessionRepository.findAll();
        var filmSessionsDto = new ArrayList<FilmSessionDto>();
        for (FilmSession session : filmSessions) {
            filmSessionsDto.add(new FilmSessionDto(filmRepository.findById(session.getFilmId()).get(), session, hallRepository.findById(session.getHallsId()).get()));
        }
        return filmSessionsDto;
    }
}
