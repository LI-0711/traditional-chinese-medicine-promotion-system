package com.example.tcmapp.controller;

import com.example.tcmapp.entity.User;
import com.example.tcmapp.service.AvatarService;
import com.example.tcmapp.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AvatarService avatarService;

    @GetMapping("/test")
    public String test() {
        return "Backend is running successfully!";
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        String message = userService.register(user);

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        String message = userService.login(user.getUsername(), user.getPassword());

        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();

        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            result.put("username", user.getUsername());
            result.put("email", user.getEmail());
            result.put("createdAt", user.getCreatedAt());
            if (user.getAvatarFilename() != null && !user.getAvatarFilename().isBlank()) {
                result.put("avatarUrl", "/user/avatar?username="
                        + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8));
            }
        } else {
            result.put("message", "用户不存在");
        }

        return result;
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam String username,
            @RequestParam("avatar") MultipartFile avatar) {
        Map<String, Object> result = new HashMap<>();
        try {
            avatarService.saveAvatar(username, avatar);
            result.put("message", "头像上传成功");
            result.put("avatarUrl", "/user/avatar?username="
                    + URLEncoder.encode(username, StandardCharsets.UTF_8));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException exception) {
            result.put("message", exception.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (IOException exception) {
            result.put("message", "头像保存失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/avatar")
    public ResponseEntity<Resource> getAvatar(@RequestParam String username) {
        try {
            AvatarService.AvatarFile avatar = avatarService.getAvatar(username);
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.noCache())
                    .contentType(avatar.mediaType())
                    .body(avatar.resource());
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.notFound().build();
        } catch (IOException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
