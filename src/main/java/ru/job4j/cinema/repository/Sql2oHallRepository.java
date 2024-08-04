package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.model.Ticket;

import java.util.Collection;
import java.util.Optional;
@Repository
public class Sql2oHallRepository implements HallRepository {
    private static final Logger LOG = LoggerFactory.getLogger(Sql2oUserRepository.class.getName());

    private final Sql2o sql2o;

    public Sql2oHallRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }


    @Override
    public Hall save(Hall hall) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO halls(name, row_count, place_count, description)
                      VALUES (:name, :rowCount, :placeCount, :description)
                      """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", hall.getName())
                    .addParameter("rowCount", hall.getRowCount())
                    .addParameter("placeCount", hall.getPlaceCount())
                    .addParameter("description", hall.getDescription());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            hall.setId(generatedId);
            return hall;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM halls WHERE id = :id");
            query.addParameter("id", id);
            var result = query.executeUpdate().getResult() > 0;
            return result;
        }
    }

    @Override
    public Optional<Hall> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM halls WHERE id = :id");
            query.addParameter("id", id);
            var hall = query.setColumnMappings(Hall.COLUMN_MAPPING).executeAndFetchFirst(Hall.class);
            return Optional.ofNullable(hall);
        }
    }

    @Override
    public Collection<Hall> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM halls");
            return query.setColumnMappings(Hall.COLUMN_MAPPING).executeAndFetch(Hall.class);
        }
    }
}
