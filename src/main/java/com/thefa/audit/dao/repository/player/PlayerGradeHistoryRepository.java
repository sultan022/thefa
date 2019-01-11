package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.history.PlayerGradeHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerGradeHistoryRepository extends PagingAndSortingRepository<PlayerGradeHistory, Long> {

    @Query("SELECT h FROM PlayerGradeHistory h WHERE h.fanId = :fanId ORDER BY h.createdAt")
    List<PlayerGradeHistory> findAllByFanId(@Param("fanId") Long fanId);
}
