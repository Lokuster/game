package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PlayerController {
    private final PlayService playService;

    @Autowired
    public PlayerController(PlayService playService) {
        this.playService = playService;
    }

    @GetMapping("/rest/players")
    public List<Player> getPlayers(@RequestParam(name = "pageNumber", defaultValue = "0") int pageNumber,
                                   @RequestParam(name = "pageSize", defaultValue = "3") int pageSize,
                                   @RequestParam(name = "order", defaultValue = "ID") String order,
                                   @RequestParam(name = "name", defaultValue = "") String name,
                                   @RequestParam(name = "title", defaultValue = "") String title,
                                   @RequestParam(name = "after", defaultValue = "0") long after,
                                   @RequestParam(name = "before", defaultValue = "9223372036854775807") long before,
                                   @RequestParam(name = "banned", defaultValue = "") String banned,
                                   @RequestParam(name = "race", defaultValue = "") String race,
                                   @RequestParam(name = "profession", defaultValue = "") String profession,
                                   @RequestParam(name = "minExperience", defaultValue = "0") int minExperience,
                                   @RequestParam(name = "maxExperience", defaultValue = "10000000") int maxExperience,
                                   @RequestParam(name = "minLevel", defaultValue = "0") int minLevel,
                                   @RequestParam(name = "maxLevel", defaultValue = "100000") int maxLevel) {
        return playService.getEntities(pageNumber, pageSize, order, name, title, after, before, banned, race, profession,
                minExperience, maxExperience, minLevel, maxLevel);
    }

    @PostMapping("/rest/players")
    public Player createPlayer(@RequestBody Map<String, String> newPlayer) {
        return playService.createEntity(newPlayer);
    }

    @GetMapping("/rest/players/count")
    public Long getCountOfPlayers(@RequestParam(name = "name", defaultValue = "") String name,
                                  @RequestParam(name = "title", defaultValue = "") String title,
                                  @RequestParam(name = "after", defaultValue = "0") long after,
                                  @RequestParam(name = "before", defaultValue = "9223372036854775807") long before,
                                  @RequestParam(name = "banned", defaultValue = "") String banned,
                                  @RequestParam(name = "race", defaultValue = "") String race,
                                  @RequestParam(name = "profession", defaultValue = "") String profession,
                                  @RequestParam(name = "minExperience", defaultValue = "0") int minExperience,
                                  @RequestParam(name = "maxExperience", defaultValue = "10000000") int maxExperience,
                                  @RequestParam(name = "minLevel", defaultValue = "0") int minLevel,
                                  @RequestParam(name = "maxLevel", defaultValue = "100000") int maxLevel) {
        return playService.getCount(name, title, after, before, banned, race, profession,
                minExperience, maxExperience, minLevel, maxLevel);
    }

    @PostMapping("/rest/players/{id}")
    public Player updatePlayer(@RequestBody Map<String, String> updatedPlayer,
                               @PathVariable long id) {
        return playService.updateEntity(updatedPlayer, id);
    }

    @GetMapping("/rest/players/{id}")
    public Player getPlayer(@PathVariable long id) {
        return playService.getEntityById(id);
    }

    @DeleteMapping("/rest/players/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playService.deleteById(id);
    }
}
