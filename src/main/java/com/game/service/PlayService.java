package com.game.service;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayService {
    List<Player> getEntities(int pageNumber, int pageSize, String order,
                             String name, String title, long after,
                             long before, String banned, String race,
                             String profession, int minExperience, int maxExperience,
                             int minLevel, int maxLevel);

    Player createEntity(Map<String, String> newPlayer);

    void deleteById(long id);

    Player updateEntity(Map<String, String> updatedPlayer, long id);

    Long getCount(String name, String title, long after,
                  long before, String banned, String race,
                  String profession, int minExperience, int maxExperience,
                  int minLevel, int maxLevel);

    Player getEntityById(long id);
}
