package ru.job4j.cinema.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;


import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    private UserController userController;
    private UserService userService;

    private HttpSession session;
    private MockHttpServletRequest request;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        session = new MockHttpSession();
        request = new MockHttpServletRequest();
    }

    @Test
    public void whenRegisterTest() {
        var user = new User(1,  "Ivan", "123@mail.ru","pass");
        var expectedUser = Optional.ofNullable(user);
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        when(userService.save(userArgumentCaptor.capture())).thenReturn(expectedUser);

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenRegisterTestTestWaitingErrorMessage() {
        var expectedException = new RuntimeException("Пользователь с такой почтой уже существует");
        when(userService.save(any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = userController.register(model, new User());
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenGetCreationPageTest() {
        var model = new ConcurrentModel();
        var view = userController.getCreationPage(model, session);

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenGetLoginPageTest() {
        var model = new ConcurrentModel();
        var view = userController.getLoginPage(model, session);

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLogoutTest() {
        var model = new ConcurrentModel();
        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenLoginUserTest() {
        var user = new User(1,  "Ivan", "123@mail.ru","pass");
        var expectedUser = Optional.ofNullable(user);

        when(userService.findByEmailAndPassword(any(), any())).thenReturn(expectedUser);

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var actualUser = request.getSession().getAttribute("user");

        assertThat(view).isEqualTo("redirect:/index");
        assertThat(actualUser).isEqualTo(expectedUser.get());
    }

    @Test
    public void whenLoginUserTestWaitingErrorMessage() {
        var expectedException = new RuntimeException("Почта или пароль введены неверно");
        when(userService.findByEmailAndPassword(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = userController.loginUser(new User(), model, request);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }





}
