package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.repository.GenreRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;
@Service
@ThreadSafe
public class SimpleGenreService implements GenreService {
    private final GenreRepository genreRepository;

    public SimpleGenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public Genre save(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public boolean deleteById(int id) {
        return genreRepository.deleteById(id);
    }

    @Override
    public Optional<Genre> findById(int id) {
        return genreRepository.findById(id);
    }

    @Override
    public Collection<Genre> findAll() {
        return genreRepository.findAll();
    }
}
