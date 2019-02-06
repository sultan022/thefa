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

    @Query("FROM PlayerForeignMapping WHERE source = :source AND foreignPlayerId IS NOT NULL")
    Set<PlayerForeignMapping> findPlayers(@Param("source") DataSourceType source);

}
