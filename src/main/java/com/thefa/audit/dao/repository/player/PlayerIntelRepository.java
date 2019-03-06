package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.PlayerIntel;
import com.thefa.audit.model.shared.IntelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerIntelRepository extends PagingAndSortingRepository<PlayerIntel, Long> {

    Page<PlayerIntel> findByPlayerIdAndIntelTypeAndArchivedIsFalse(String playerId, IntelType intelType, Pageable pageable);

    Page<PlayerIntel> findByPlayerIdAndArchivedIsFalse(String playerId, Pageable pageable);

    Page<PlayerIntel> findByPlayerIdAndIntelType(String playerId, IntelType intelType, Pageable pageable);

    Page<PlayerIntel> findByPlayerId(String playerId, Pageable pageable);

    long countByPlayerIdAndIdInAndArchivedIsFalse(String playerId, List<Long> ids);

    List<PlayerIntel> findByPlayerId(String playerId);

}
