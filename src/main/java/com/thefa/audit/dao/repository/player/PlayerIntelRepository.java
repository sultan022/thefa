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

    Page<PlayerIntel> findByFanIdAndIntelTypeAndArchivedIsFalse(long fanId, IntelType intelType, Pageable pageable);

    Page<PlayerIntel> findByFanIdAndArchivedIsFalse(long fanId, Pageable pageable);

    long countByFanIdAndIdInAndArchivedIsFalse(long fanId, List<Long> ids);

    List<PlayerIntel> findByFanId(long fanId);

}
