package ru.job4j.cinema.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.cinema.model.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
@Repository
public class Sql2oFileRepository implements FileRepository {

    private static final Logger LOG = LoggerFactory.getLogger(Sql2oFileRepository.class.getName());

    private final Sql2o sql2o;

    public Sql2oFileRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }
    @Override
    public Optional<File> save(File file) {
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO files(name, path)
                      VALUES (:name, :path)
                      """;
            var query = connection.createQuery(sql, true)
                    .addParameter("name", file.getName())
                    .addParameter("path", file.getPath());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            file.setId(generatedId);
            return Optional.ofNullable(file);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<File> findById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM files WHERE id = :id");
            query.addParameter("id", id);
            var file = query.addParameter("id", id).executeAndFetchFirst(File.class);
            return Optional.ofNullable(file);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM files WHERE id = :id");
            query.addParameter("id", id).executeUpdate();
            var result = query.executeUpdate().getResult() > 0;
            return result;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Collection<File> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM files");
            return query.executeAndFetch(File.class);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return new ArrayList<File>();
    }
}
