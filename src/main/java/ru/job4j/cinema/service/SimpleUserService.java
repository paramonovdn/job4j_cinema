package ru.job4j.cinema.service;

import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.repository.UserRepository;


import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;
@Service
@ThreadSafe
public class SimpleUserService implements UserService {
    private final UserRepository userRepository;


    public SimpleUserService(UserRepository userReposytory) {
        this.userRepository = userReposytory;
    }


    @Override
    public Optional<User> save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean deleteById(int id) {
        return userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }
}
