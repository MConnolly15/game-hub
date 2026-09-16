package com.example.gamehub.service;

import com.example.gamehub.model.User;
import com.example.gamehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateUsername(String email, String newUsername){
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setUsername(newUsername);
    }
}

