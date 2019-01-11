package com.thefa.audit.dao.service;

import com.thefa.audit.model.entity.player.PlayerInternalMappingCounter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.transaction.Transactional;

@Service
@Transactional
@CommonsLog
public class PlayerInternalMappingCounterService {

    public static final String INTERNAL_ID_PREFIX = "fapl000";

    private final EntityManager entityManager;

    @Autowired
    public PlayerInternalMappingCounterService(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public String getNextCounter() {

        PlayerInternalMappingCounter counter = entityManager.find(PlayerInternalMappingCounter.class, 1L,
                LockModeType.PESSIMISTIC_WRITE);
        long newCounter = counter.getCounter() + 1;
        counter.setCounter(newCounter);
        entityManager.persist(counter);
        return INTERNAL_ID_PREFIX + newCounter;
    }
}
