package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PlayerServiceImpl implements PlayService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getEntities(int pageNumber, int pageSize, String order,
                                    String name, String title, long after,
                                    long before, String banned, String race, String profession,
                                    int minExperience, int maxExperience, int minLevel,
                                    int maxLevel) {
        switch (order) {
            case "ID":
                return getFilteredEntitiesById(pageNumber, pageSize, name, title, after, before, banned, race, profession,
                        minExperience, maxExperience, minLevel, maxLevel);
            case "NAME":
                return getFilteredEntitiesByName(pageNumber, pageSize, name, title, after, before, banned, race, profession,
                        minExperience, maxExperience, minLevel, maxLevel);
            case "EXPERIENCE":
                return getFilteredEntitiesByExperience(pageNumber, pageSize, name, title, after, before, banned, race, profession,
                        minExperience, maxExperience, minLevel, maxLevel);
            case "BIRTHDAY":
                return getFilteredEntitiesByBirthday(pageNumber, pageSize, name, title, after, before, banned, race, profession,
                        minExperience, maxExperience, minLevel, maxLevel);
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    private Stream<Player> getFilteredEntities(String name, String title, long after,
                                              long before, String banned, String race,
                                              String profession, int minExperience, int maxExperience,
                                              int minLevel, int maxLevel) {
        return playerRepository.findByNameContainingAndTitleContaining(name, title).stream()
                .filter(x -> x.getBirthday().getTime() >= after && x.getBirthday().getTime() <= before)
                .filter(x -> banned.equals("") || x.isBanned() == banned.equals("true"))
                .filter(x -> race.equals("") || x.getRace().toString().equals(race))
                .filter(x -> profession.equals("") || x.getProfession().toString().equals(profession))
                .filter(x -> x.getExperience() >= minExperience && x.getExperience() <= maxExperience)
                .filter(x -> x.getLevel() >= minLevel && x.getLevel() <= maxLevel);
    }

    private List<Player> getFilteredEntitiesById(int pageNumber, int pageSize, String name,
                                                String title, long after, long before,
                                                String banned, String race, String profession,
                                                int minExperience, int maxExperience, int minLevel,
                                                int maxLevel) {
        return getFilteredEntities(name, title, after, before, banned, race, profession, minExperience, maxExperience, minLevel, maxLevel)
                .sorted(Comparator.comparingLong(Player::getId))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private List<Player> getFilteredEntitiesByName(int pageNumber, int pageSize, String name,
                                                  String title, long after, long before,
                                                  String banned, String race, String profession,
                                                  int minExperience, int maxExperience, int minLevel,
                                                  int maxLevel) {
        return getFilteredEntities(name, title, after, before, banned, race, profession, minExperience, maxExperience, minLevel, maxLevel)
                .sorted(Comparator.comparing(Player::getName))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private List<Player> getFilteredEntitiesByExperience(int pageNumber, int pageSize, String name,
                                                        String title, long after, long before,
                                                        String banned, String race, String profession,
                                                        int minExperience, int maxExperience, int minLevel,
                                                        int maxLevel) {
        return getFilteredEntities(name, title, after, before, banned, race, profession, minExperience, maxExperience, minLevel, maxLevel)
                .sorted(Comparator.comparingLong(Player::getExperience))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }

    private List<Player> getFilteredEntitiesByBirthday(int pageNumber, int pageSize, String name,
                                                      String title, long after, long before,
                                                      String banned, String race, String profession,
                                                      int minExperience, int maxExperience, int minLevel,
                                                      int maxLevel) {
        return getFilteredEntities(name, title, after, before, banned, race, profession, minExperience, maxExperience, minLevel, maxLevel)
                .sorted(Comparator.comparing(Player::getBirthday))
                .skip((long) pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }


    @Override
    public Player createEntity(Map<String, String> newPlayer) {
        if (newPlayer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        for (String key : newPlayer.keySet()) {
            if (newPlayer.get(key).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
        Player player = new Player();
        playerRepository.saveAndFlush(newEntity(newPlayer, player));
        return player;
    }

    @Override
    public void deleteById(long id) {
        playerRepository.delete(getEntityById(id));
        playerRepository.flush();
    }

    @Override
    public Player updateEntity(Map<String, String> updatedPlayer, long id) {
        Player player = newEntity(updatedPlayer, getEntityById(id));
        playerRepository.saveAndFlush(player);
        return player;
    }

    private Player newEntity(Map<String, String> updatedPlayer, Player playerToEdit) {
        for (String key : updatedPlayer.keySet()) {
            String value = updatedPlayer.get(key);
            switch (key) {
                case "name":
                    if (value.length() > 12 || value.equals("")) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                    } else {
                        playerToEdit.setName(value);
                    }
                    break;
                case "title":
                    if (value.length() > 30) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                    } else {
                        playerToEdit.setTitle(value);
                    }
                    break;
                case "race":
                    if (!Arrays.stream(Race.values()).map(Enum::toString).collect(Collectors.toList()).contains(value)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                    }
                    playerToEdit.setRace(Race.valueOf(value));
                    break;
                case "profession":
                    if (!Arrays.stream(Profession.values()).map(Enum::toString).collect(Collectors.toList()).contains(value)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                    }
                    playerToEdit.setProfession(Profession.valueOf(value));
                    break;
                case "birthday":
                    Date date = new Date(Long.parseLong(value));
                    if (date.getYear() + 1900 >= 2000 && date.getYear() <= 3000 + 1900) {
                        playerToEdit.setBirthday(date);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                    }
                    break;
                case "banned":
                    playerToEdit.setBanned(value.equals("true"));
                    break;
                case "experience":
                    int experience = Integer.parseInt(value);
                    if (experience >= 0 && experience <= 10_000_000) {
                        playerToEdit.setExperience(experience);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                    }
                    break;
            }
        }
        return playerToEdit;
    }


    @Override
    public Long getCount(String name, String title, long after,
                         long before, String banned, String race,
                         String profession, int minExperience, int maxExperience,
                         int minLevel, int maxLevel) {
        return getFilteredEntities(name, title, after, before, banned, race, profession,
                minExperience, maxExperience, minLevel, maxLevel).count();
    }

    @Override
    public Player getEntityById(long id) {
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!playerRepository.findById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return playerRepository.findById(id).get();
    }

}
