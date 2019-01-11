package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.PlayerForeignId;
import com.thefa.audit.model.entity.player.PlayerForeignMapping;
import com.thefa.audit.model.shared.DataSourceType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PlayerForeignMappingRepository extends PagingAndSortingRepository<PlayerForeignMapping, PlayerForeignId> {

    @Query("SELECT p FROM PlayerForeignMapping p WHERE p.source = :source AND p.foreignPlayerId IN :playerIds")
    Set<PlayerForeignMapping> findPlayersMapping(@Param("source") DataSourceType source, @Param("playerIds") Set<String> playerIds);
}
