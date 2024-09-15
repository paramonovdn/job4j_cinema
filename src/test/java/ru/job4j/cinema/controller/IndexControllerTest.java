package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    public void initServices() {
        indexController = new IndexController();
    }

    @Test
    public void test() {
        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }
}
