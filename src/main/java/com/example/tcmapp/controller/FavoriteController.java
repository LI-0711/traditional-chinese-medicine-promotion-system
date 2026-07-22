package com.example.tcmapp.controller;

import com.example.tcmapp.entity.Favorite;
import com.example.tcmapp.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    // 添加收藏
    @PostMapping("/add")
    public Favorite addFavorite(@RequestBody Favorite favorite) {
        return favoriteService.save(favorite);
    }

    // 查询收藏列表
    @GetMapping("/list")
    public List<Favorite> getFavorites(@RequestParam String username) {
        return favoriteService.getFavorites(username);
    }

    // 取消收藏
    @DeleteMapping("/remove")
    public void removeFavorite(
            @RequestParam String username,
            @RequestParam String herbName) {

        favoriteService.removeFavorite(
                username,
                herbName
        );
    }
}