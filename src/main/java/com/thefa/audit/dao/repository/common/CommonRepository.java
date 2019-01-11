package com.thefa.audit.dao.repository.common;

import com.thefa.audit.model.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface CommonRepository extends JpaRepository<Player, Long> {

    @Query("SELECT count(*) FROM PlayerForeignMapping WHERE foreignPlayerId IN :playerIds AND source = 'INTERNAL'")
    int fanIdsExistForGivenPlayerIdsForInternalSource(List<String> playerIds);
}
