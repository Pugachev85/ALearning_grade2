package com.ALearning_grade2.service.impl;

import com.ALearning_grade2.dao.UserDao;
import com.ALearning_grade2.entity.User;
import com.ALearning_grade2.exception.InvalidUserException;
import com.ALearning_grade2.service.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(User user) throws InvalidUserException {
        validateUser(user);
        userDao.create(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public void updateUser(User user) throws InvalidUserException {
        validateUser(user);
        userDao.update(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userDao.delete(id);
    }

    private void validateUser(User user) throws InvalidUserException {
        if (user.getName() == null
                || user.getName().trim().isEmpty()) {
            throw new InvalidUserException("Имя пользователя не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new InvalidUserException("Email не может быть пустым");
        }
        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new InvalidUserException("Некорректный формат email");
        }
        if (user.getAge() < 0 || user.getAge() > 150) {
            throw new InvalidUserException("Возраст должен быть в диапазоне 0–150");
        }
    }
}