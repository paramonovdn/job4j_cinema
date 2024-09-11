package ru.job4j.cinema.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.model.File;


import java.util.List;
import java.util.Properties;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oFileRepositoryTest {
    private static Sql2oFileRepository sql2oFileRepository;


    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oFileRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFileRepository = new Sql2oFileRepository(sql2o);

    }

    @AfterEach
    public void clearFiles() {
        var files = sql2oFileRepository.findAll();
       for (var file : files) {
           sql2oFileRepository.deleteById(file.getId());
       }
    }

    @Test
    public void whenSaveThenGetSame() {
        var savedFile = new File("test", "test");
        sql2oFileRepository.save(savedFile);
        var extectedFile = sql2oFileRepository.findById(savedFile.getId()).get();

        assertThat(savedFile).usingRecursiveComparison().isEqualTo(extectedFile);
    }

    @Test
    public void whenSaveThenGetSeveralSame() {
        var savedFile1 = new File("test1", "test1");
        var savedFile2 = new File("test2", "test2");
        var savedFile3 = new File("test3", "test3");
        sql2oFileRepository.save(savedFile1);
        sql2oFileRepository.save(savedFile2);
        sql2oFileRepository.save(savedFile3);
        var extectedFile1 = sql2oFileRepository.findById(savedFile1.getId()).get();
        var extectedFile2 = sql2oFileRepository.findById(savedFile2.getId()).get();
        var extectedFile3 = sql2oFileRepository.findById(savedFile3.getId()).get();

        assertThat(savedFile1).usingRecursiveComparison().isEqualTo(extectedFile1);
        assertThat(savedFile2).usingRecursiveComparison().isEqualTo(extectedFile2);
        assertThat(savedFile3).usingRecursiveComparison().isEqualTo(extectedFile3);
    }

    @Test
    public void whenDeleteThenGetSeveralSame() {
        var savedFile1 = new File("test1", "test1");
        var savedFile2 = new File("test2", "test2");
        var savedFile3 = new File("test3", "test3");
        sql2oFileRepository.save(savedFile1);
        sql2oFileRepository.save(savedFile2);
        sql2oFileRepository.save(savedFile3);

        assertThat(sql2oFileRepository.findAll().size()).isEqualTo(3);
        sql2oFileRepository.deleteById(savedFile1.getId());
        assertThat(sql2oFileRepository.findAll().size()).isEqualTo(2);
        sql2oFileRepository.deleteById(savedFile2.getId());
        assertThat(sql2oFileRepository.findAll().size()).isEqualTo(1);
        sql2oFileRepository.deleteById(savedFile3.getId());
        assertThat(sql2oFileRepository.findAll().size()).isEqualTo(0);
    }


    @Test
    public void whenSaveSeveralThenGetAll() {
        var savedFile1 = new File("test1", "test1");
        var savedFile2 = new File("test2", "test2");
        var savedFile3 = new File("test3", "test3");
        sql2oFileRepository.save(savedFile1);
        sql2oFileRepository.save(savedFile2);
        sql2oFileRepository.save(savedFile3);
        var result = sql2oFileRepository.findAll();
        assertThat(result).isEqualTo(List.of(savedFile1, savedFile2, savedFile3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oFileRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oFileRepository.findById(0)).isEqualTo(empty());
    }
}
