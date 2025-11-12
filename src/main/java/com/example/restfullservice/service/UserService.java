package com.example.restfullservice.service;

import com.example.restfullservice.model.dto.UserCreateRequest;
import com.example.restfullservice.model.entity.User;
import com.example.restfullservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Log4j2
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(UserCreateRequest request) {
        if (request == null
                || !StringUtils.hasText(request.name())
                || !StringUtils.hasText(request.email())
        ) {
            throw new IllegalArgumentException("Обязательные поля не заполнены");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Пользователь с таким + " + request.email() + " уже существует!"
                    + "\nПроверьте корректность вводимого вами Email");
        }
        User savedUser = userRepository.save(new User(request.name(), request.email(), request.birthDate()));
        log.info("Пользователь сохранен {}", savedUser);
        return savedUser;
    }

    public void delete(Long id) {
        if (userRepository.existsById(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new IllegalStateException("Пользователь не найден!");
        }
        userRepository.deleteById(id);
        log.info("Пользователь с id {} удален", id);
    }

    @Transactional
    public void update(Long id, String email, String name) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("Пользователь с id {} не найден", id);
            return new IllegalStateException("Пользователь не существует в базе данных!"
                    + "\nПроверьте корректность вводимого вами ID");
        });
        boolean isChanged = false;
        if (StringUtils.hasText(email) && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new IllegalArgumentException("Пользователь с таким Email уже существует!"
                        + "\nПроверьте корректность вводимого вами Email");
            }
            user.setEmail(email);
            isChanged = true;
        }
        if (StringUtils.hasText(name) && !name.equals(user.getName())) {
            user.setName(name);
            isChanged = true;
        }
        if (isChanged) {
            User savedUser = userRepository.save(user);
            log.info("Пользователь обновлен {}", savedUser);
        }
    }
}
