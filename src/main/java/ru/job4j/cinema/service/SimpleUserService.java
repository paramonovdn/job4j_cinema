package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserReposytory;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;
@Service
@ThreadSafe
public class SimpleUserService implements UserService {
    private final UserReposytory userReposytory;


    public SimpleUserService(UserReposytory userReposytory) {
        this.userReposytory = userReposytory;
    }


    @Override
    public Optional<User> save(User user) {
        return userReposytory.save(user);
    }

    @Override
    public boolean deleteById(int id) {
        return userReposytory.deleteById(id);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userReposytory.findByEmailAndPassword(email, password);
    }

    @Override
    public Collection<User> findAll() {
        return userReposytory.findAll();
    }
}
