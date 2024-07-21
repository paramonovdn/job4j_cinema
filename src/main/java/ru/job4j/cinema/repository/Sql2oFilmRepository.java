package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Film;

import java.util.Collection;
import java.util.Optional;
@Repository
public class Sql2oFilmRepository implements FilmRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Sql2oUserRepository.class.getName());

    private final Sql2o sql2o;

    public Sql2oFilmRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }
    @Override
    public Film save(Film film) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO films(name, description, year, genre_id, minimal_age, duration_in_minutes, file_id)
                      VALUES (:name, :description, :year, :genreId, :minimalAge, :durationInMinutes, :fileId)
                      """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getGenreId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinute())
                    .addParameter("fileId", film.getFileId());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            film.setId(generatedId);
            return film;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM films WHERE id = :id");
            query.addParameter("id", id);
            var result = query.executeUpdate().getResult() > 0;
            return result;
        }
    }

    @Override
    public boolean update(Film film) {
        try (var connection = sql2o.open()) {
            var sql = """
                    UPDATE films
                    SET name = :name, description = :description, year = :year, genre_id = :genreId,
                        minimal_age = :minimalAge, duration_in_minutes = :durationInMinutes, file_id = :fileId
                    WHERE id = :id
                    """;
            var query = connection.createQuery(sql)
                    .addParameter("name", film.getName())
                    .addParameter("description", film.getDescription())
                    .addParameter("year", film.getYear())
                    .addParameter("genreId", film.getId())
                    .addParameter("minimalAge", film.getMinimalAge())
                    .addParameter("durationInMinutes", film.getDurationInMinute())
                    .addParameter("fileId", film.getFileId())
                    .addParameter("Id", film.getId());
            var affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }

    @Override
    public Optional<Film> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM films WHERE id = :id");
            query.addParameter("id", id);
            var film = query.setColumnMappings(Film.COLUMN_MAPPING).executeAndFetchFirst(Film.class);
            return Optional.ofNullable(film);
        }
    }

    @Override
    public Collection<Film> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM films");
            return query.executeAndFetch(Film.class);
        }
    }
}
