package com.example.restfullservice.service;

import com.example.restfullservice.repository.User;
import com.example.restfullservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        Optional<User> beEmail = userRepository.findByEmail(user.getEmail());
        if (beEmail.isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким + " + beEmail + " уже существует!"
                    + "\nПроверьте корректность вводимого вами Email");
        }
        user.setAge(Period.between(user.getBirth(), LocalDate.now()).getYears());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Пользователя с таким " + id + " не существует в базе данных!"
                    + "\nПроверьте корректность вводимого вами ID");
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, String email, String name) {
        Optional<User> optionalUserId = userRepository.findById(id);
        if (optionalUserId.isEmpty()) {
            throw new IllegalStateException("Пользователя с таким " + id + " не существует в базе данных!"
                    + "\nПроверьте корректность вводимого вами ID");
        }
        User user = optionalUserId.get();
        if (email != null && !email.equals(user.getEmail())) {
            Optional<User> foundByEmail = userRepository.findByEmail(email);
            if (foundByEmail.isPresent()) {
                throw new IllegalArgumentException("Пользователь с таким Email уже существует!"
                        + "\nПроверьте корректность вводимого вами Email");
            }
            user.setEmail(email);
        }
        if (name != null && !name.equals(user.getName())) {
            user.setName(name);
            userRepository.save(user);

        }
    }
}
