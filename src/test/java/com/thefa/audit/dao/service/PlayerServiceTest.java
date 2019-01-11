package com.thefa.audit.dao.service;

import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.common.dto.shared.PageResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertTrue;

public class PlayerServiceTest extends AbstractIntegrationTest {

    @Autowired
    private PlayerService playerService;

    @Test
    public void findPlayersMethodShouldReturnCorrectResult() {

        PageResponse response = playerService.findPlayers(0, 100, null);

        assertTrue("Incorrect total size", response.getTotalSize() >= 1);
        assertTrue("Incorrect total length", response.getContent().size() >= 1);

    }

}
