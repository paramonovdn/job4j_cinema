package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.dto.FilmDto;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.repository.FileRepository;
import ru.job4j.cinema.repository.FilmRepository;
import ru.job4j.cinema.repository.GenreRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
@Service
@ThreadSafe
public class SimpleFilmService implements FilmService {

    private final FilmRepository filmRepository;

    private final FileService fileService;

    private final GenreRepository genreRepository;

    public SimpleFilmService(FilmRepository filmRepository, FileService fileService, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.fileService = fileService;
        this.genreRepository = genreRepository;
    }

    @Override
    public Film save(Film film, FileDto image) {
        saveNewFile(film, image);
        return filmRepository.save(film);
    }

    private void saveNewFile(Film film, FileDto image) {
        var file = fileService.save(image);
        film.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var fileOptional = findById(id);
        if (fileOptional.isPresent()) {
            filmRepository.deleteById(id);
            fileService.deleteById(fileOptional.get().getId());
            return  true;
        }
        return false;
    }

    @Override
    public boolean update(Film film, FileDto image) {
        var isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return filmRepository.update(film);
        }
        /* если передан новый не пустой файл, то старый удаляем, а новый сохраняем */
        var oldFileId = film.getFileId();
        saveNewFile(film, image);
        var isUpdated = filmRepository.update(film);
        fileService.deleteById(oldFileId);
        return isUpdated;
    }

    @Override
    public Optional<FilmDto> findById(int id) {
        var film = filmRepository.findById(id).get();
        var genre = genreRepository.findById(film.getGenreId()).get();

        return Optional.ofNullable(new FilmDto(film, genre));
    }

    @Override
    public Collection<FilmDto> findAll() {
        var films = filmRepository.findAll();
        var filmsDto = new ArrayList<FilmDto>();
        for (Film film : films) {
            filmsDto.add(new FilmDto(film, genreRepository.findById(film.getGenreId()).get()));
        }
        return filmsDto;
    }
}
