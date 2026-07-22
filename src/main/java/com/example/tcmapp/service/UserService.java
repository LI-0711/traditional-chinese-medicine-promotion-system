package com.example.tcmapp.service;

import com.example.tcmapp.entity.User;
import com.example.tcmapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 注册用户
    public String register(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            return "用户名已存在";
        }

        // 密码加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "注册成功";
    }

    // 根据用户名查找
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 登录校验
    public String login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return "用户名不存在";
        }

        User user = optionalUser.get();

        // 用 matches 比对明文和加密密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "密码错误";
        }

        return "登录成功";
    }
}