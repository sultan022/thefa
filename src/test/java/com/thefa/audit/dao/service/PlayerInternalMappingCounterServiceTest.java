package com.thefa.audit.dao.service;

import com.thefa.audit.config.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class PlayerInternalMappingCounterServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PlayerInternalMappingCounterService playerInternalMappingCounterService;

    @Test
    public void playerCounterShouldReturnCorrectValue() {

        String nextCounter = playerInternalMappingCounterService.getNextCounter();

        Long counterLong = Long.valueOf(nextCounter.replace(PlayerInternalMappingCounterService.INTERNAL_ID_PREFIX, ""));

        assertTrue("Incorrect Counter Prefix", nextCounter.startsWith(PlayerInternalMappingCounterService.INTERNAL_ID_PREFIX));
        assertTrue("Incorrect Counter Value", counterLong > 11000L);

    }
}
