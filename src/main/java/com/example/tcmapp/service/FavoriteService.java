package com.example.tcmapp.service;

import com.example.tcmapp.entity.Favorite;
import com.example.tcmapp.repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository repository;

    public FavoriteService(FavoriteRepository repository) {
        this.repository = repository;
    }

    // 收藏
    public Favorite save(Favorite favorite) {

        Favorite exist =
                repository.findByUsernameAndHerbName(
                        favorite.getUsername(),
                        favorite.getHerbName()
                );

        // 已经收藏过
        if (exist != null) {
            return exist;
        }

        return repository.save(favorite);
    }

    // 查询收藏
    public List<Favorite> getFavorites(String username) {
        return repository.findByUsername(username);
    }

    // 取消收藏
    public void removeFavorite(
            String username,
            String herbName) {

        Favorite favorite =
                repository.findByUsernameAndHerbName(
                        username,
                        herbName
                );

        if (favorite != null) {
            repository.delete(favorite);
        }
    }
}