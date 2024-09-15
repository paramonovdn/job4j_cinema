package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cinema.dto.FileDto;
import ru.job4j.cinema.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileControllerTest {

    private FileService fileService;
    private FileController fileController;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenReturnNotNullResponse() throws IOException {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var expectedFileDto = Optional.ofNullable(fileDto);
        when(fileService.getFileById(1)).thenReturn(expectedFileDto);

        var view = fileController.getById(1);

        assertThat(view).isEqualTo(ResponseEntity.ok(expectedFileDto.get().getContent()));
    }

    @Test
    public void whenReturnNullResponse() throws IOException {
        when(fileService.getFileById(1)).thenReturn(Optional.empty());

        var view = fileController.getById(1);

        assertThat(view).isEqualTo(ResponseEntity.notFound().build());
    }
}
