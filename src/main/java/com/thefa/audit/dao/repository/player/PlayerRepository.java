package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.shared.SquadType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

    Stream<Player> findAllByPlayerIdIn(List<String> playerIds);

    @Query("SELECT COUNT(p) FROM Player p LEFT JOIN p.playerSquads s WHERE p.playerId IN :playerIds AND s.squad = :squad")
    long countByPlayerIdInAndPlayerSquadsSquadTypeContaining(@Param("playerIds") Set<String> playerId, @Param("squad") SquadType squadType);

    long countByPlayerIdIn(Set<String> playerId);

}
