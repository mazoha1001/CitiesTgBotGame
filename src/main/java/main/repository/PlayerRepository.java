package main.repository;

import main.entity.Player;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query("SELECT p FROM Player p where p.id = :id")
    Player getPlayerById(String id);

    @Query(value = "SELECT p FROM Player p ORDER BY p.highScore DESC")
    List<Player> findTopPlayersByScore(Limit limit);
}
