package ru.job4j.cinema.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.cinema.repository.Sql2oFileRepository;
import ru.job4j.cinema.configuration.DatasourceConfiguration;
import ru.job4j.cinema.dto.FileDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class SimpleFileServiceTest {

    private  static FileService fileService;

    private static Sql2oFileRepository sql2oFileRepository;
    private static String storageDirectory;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = SimpleFileServiceTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oFileRepository = new Sql2oFileRepository(sql2o);
        storageDirectory = "src/test/test";
        fileService = new SimpleFileService(sql2oFileRepository, storageDirectory);
    }

    @AfterEach
    public void clearFiles() throws IOException {
        var files = sql2oFileRepository.findAll();
        for (var file : files) {
            sql2oFileRepository.deleteById(file.getId());
        }

        File file = new File(storageDirectory);
        File[] listOfFiles = file.listFiles();
        for (var f : listOfFiles) {
            Files.deleteIfExists(Path.of(f.getPath()));
        }
    }

    @AfterAll
    public static void deletePath() throws IOException {
        FileUtils.deleteDirectory(new File(storageDirectory));
    }



    @Test
    public void whenCreateStorageDirectoryThenCheckPath() {
        var existPath = Files.exists(Path.of(storageDirectory));
        assertThat(existPath).isEqualTo(true);
    }

    @Test
    public void whenSaveFileDtoThenGetSame() throws IOException {
        byte[] fileContent = Files.readAllBytes(Path.of("files/joker.jpg"));

        var fileDto = new FileDto("joker.jpg", fileContent);
        var savedFile = fileService.save(fileDto);
        var expectedFileDto = fileService.getFileById(savedFile.getId()).get();

        assertThat(fileDto).usingRecursiveComparison().isEqualTo(expectedFileDto);
    }

  @Test
  public void whenDeleteFileThenCheckPath() throws IOException {
      byte[] fileContent = Files.readAllBytes(Path.of("files/joker.jpg"));

      var fileDto = new FileDto("joker.jpg", fileContent);
      var savedFile = fileService.save(fileDto);
      fileService.deleteById(savedFile.getId());
      var expectedFileDto = fileService.getFileById(savedFile.getId());

      File file = new File(storageDirectory);
      File[] listOfFiles = file.listFiles();
      var folderSize = listOfFiles.length;

      assertThat(expectedFileDto).isEqualTo(empty());
      assertThat(folderSize).isEqualTo(0);
  }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(fileService.getFileById(0)).isEqualTo(empty());
    }

}
