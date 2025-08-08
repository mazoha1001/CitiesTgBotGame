package main.service;

import lombok.RequiredArgsConstructor;
import main.entity.Player;
import main.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Player getPlayer(String id) {
        return playerRepository.getPlayerById(id);
    }

    public boolean isPlayerExist(String id) {
        return playerRepository.getPlayerById(id) != null;
    }

    public void savePlayer(Player player) {
        playerRepository.save(player);
    }


}
