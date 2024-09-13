package ru.job4j.cinema.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.repository.Sql2oFileRepository;
import ru.job4j.cinema.repository.Sql2oFilmRepository;
import ru.job4j.cinema.repository.Sql2oGenreRepository;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.Genre;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleFilmServiceTest {

    private  static FileService fileService;

    private static String storageDirectory;

    private static SimpleFilmService simpleFilmService;

    private static Sql2oFilmRepository sql2oFilmRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static Sql2oGenreRepository sql2oGenreRepository;

    private static File file;



    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = SimpleFilmServiceTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFilmRepository = new Sql2oFilmRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        sql2oGenreRepository = new Sql2oGenreRepository(sql2o);


        storageDirectory = "src/test/test";
        fileService = new SimpleFileService(sql2oFileRepository, storageDirectory);


        simpleFilmService = new SimpleFilmService(sql2oFilmRepository, fileService, sql2oGenreRepository);
        file = new File("joker.jpg", storageDirectory);
        sql2oFileRepository.save(file);


    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clear() throws IOException {
        var films = sql2oFilmRepository.findAll();
        for (var film : films) {
            sql2oFilmRepository.deleteById(film.getId());
        }
        var files = sql2oFileRepository.findAll();
        for (var file : files) {
            sql2oFileRepository.deleteById(file.getId());
        }
        var genres = sql2oGenreRepository.findAll();
        for (var genre : genres) {
            sql2oGenreRepository.deleteById(genre.getId());
        }
        java.io.File file = new java.io.File(storageDirectory);
        java.io.File[] listOfFiles = file.listFiles();
        for (var f : listOfFiles) {
            Files.deleteIfExists(Path.of(f.getPath()));
        }
    }


    @AfterAll
    public static void deletePath() throws IOException {
        FileUtils.deleteDirectory(new java.io.File(storageDirectory));
    }


    @Test
    public void whenSaveThenGetSame() throws IOException {
        var genre = new Genre(1, "comedy1");
        sql2oGenreRepository.save(genre);

        byte[] fileContent = Files.readAllBytes(Path.of("files/joker.jpg"));
        var fileDto = new FileDto("joker.jpg", fileContent);
        var film = simpleFilmService.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()), fileDto);
        var savedFilm = simpleFilmService.findById(film.getId()).get();

        var expectedFileDto = fileService.getFileById(film.getFileId()).get();
        var expectedFilm = new Film(savedFilm.getId(), savedFilm.getName(), savedFilm.getDescription(), savedFilm.getYear(),
                genre.getId(), savedFilm.getMinimalAge(), savedFilm.getDurationInMinutes(), film.getFileId());

        assertThat(fileDto).usingRecursiveComparison().isEqualTo(expectedFileDto);
        assertThat(expectedFilm).usingRecursiveComparison().isEqualTo(film);
    }

    @Test
    public void whenSaveSeveralThenGetAll() throws IOException {
        var genre = new Genre(1, "comedy1");
        sql2oGenreRepository.save(genre);
        byte[] fileContent1 = Files.readAllBytes(Path.of("files/joker.jpg"));
        var fileDto1 = new FileDto("joker.jpg", fileContent1);
        byte[] fileContent2 = Files.readAllBytes(Path.of("files/mechanic.jpg"));
        var fileDto2 = new FileDto("mechanic.jpg", fileContent2);
        byte[] fileContent3 = Files.readAllBytes(Path.of("files/inception.jpg"));
        var fileDto3 = new FileDto("inception.jpg", fileContent3);
        var film1 = simpleFilmService.save(new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()), fileDto1);
        var film2 = simpleFilmService.save(new Film(2, "film2", "description1", 2024, genre.getId(), 14, 220, file.getId()), fileDto2);
        var film3 = simpleFilmService.save(new Film(3, "film3", "description1", 2024, genre.getId(), 16, 140, file.getId()), fileDto3);
        var savedFilm1 = simpleFilmService.findById(film1.getId()).get();
        var savedFilm2 = simpleFilmService.findById(film2.getId()).get();
        var savedFilm3 = simpleFilmService.findById(film3.getId()).get();

        var result = simpleFilmService.findAll();
        var expected = new ArrayList<>();
        expected.add(savedFilm1);
        expected.add(savedFilm2);
        expected.add(savedFilm3);
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(simpleFilmService.findAll()).isEqualTo(emptyList());
        assertThat(simpleFilmService.findById(0)).isEqualTo(empty());
    }


    @Test
    public void whenDeleteThenGetEmptyOptional() throws IOException {
        var genre = new Genre(1, "comedy1");
        sql2oGenreRepository.save(genre);

        byte[] fileContent = Files.readAllBytes(Path.of("files/joker.jpg"));
        var fileDto = new FileDto("joker.jpg", fileContent);
        var film = simpleFilmService.save(new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()), fileDto);
        var isDeleted = simpleFilmService.deleteById(film.getId());
        var savedFilm = simpleFilmService.findById(film.getId());

        assertThat(isDeleted).isTrue();
        assertThat(savedFilm).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(simpleFilmService.deleteById(0)).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() throws IOException {
        var genre = new Genre(1, "comedy1");
        sql2oGenreRepository.save(genre);
        byte[] fileContent1 = Files.readAllBytes(Path.of("files/joker.jpg"));
        var fileDto1 = new FileDto("joker.jpg", fileContent1);
        byte[] fileContent2 = Files.readAllBytes(Path.of("files/mechanic.jpg"));
        var fileDto2 = new FileDto("mechanic.jpg", fileContent2);
        var film = simpleFilmService.save(new Film(1, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId()), fileDto1);
        var updatedFilm = new Film(film.getId(), "film2", "description1", 2024, genre.getId(), 14, 220, file.getId());
        var isUpdated = simpleFilmService.update(updatedFilm, fileDto1);
        var savedFilm = simpleFilmService.findById(updatedFilm.getId()).get();
        var expectedFilm = new Film(savedFilm.getId(), savedFilm.getName(), savedFilm.getDescription(), savedFilm.getYear(),
                genre.getId(), savedFilm.getMinimalAge(), savedFilm.getDurationInMinutes(), updatedFilm.getFileId());
        assertThat(isUpdated).isTrue();
        assertThat(expectedFilm).usingRecursiveComparison().isEqualTo(updatedFilm);
    }

    @Test
    public void whenUpdateUnExistingFilmThenGetFalse() throws IOException {
        var genre = new Genre(1, "comedy1");
        sql2oGenreRepository.save(genre);

        byte[] fileContent = Files.readAllBytes(Path.of("files/joker.jpg"));
        var fileDto = new FileDto("joker.jpg", fileContent);
        var film = new Film(0, "film1", "description1", 2024, genre.getId(), 12, 120, file.getId());


        var isUpdated = simpleFilmService.update(film, fileDto);
        assertThat(isUpdated).isFalse();
    }

}
