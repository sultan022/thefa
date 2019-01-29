package com.thefa.audit.controller;

import com.thefa.audit.config.AbstractIntegrationTest;
import com.thefa.audit.dao.repository.player.PlayerGradeHistoryRepository;
import com.thefa.audit.dao.service.PlayerForeignMappingService;
import com.thefa.audit.model.dto.player.base.PlayerDTO;
import com.thefa.audit.model.dto.player.base.PlayerForeignMappingDTO;
import com.thefa.audit.model.dto.player.create.CreatePlayerDTO;
import com.thefa.audit.model.dto.player.load.PlayerGradeUploadDTO;
import com.thefa.audit.model.entity.history.PlayerGradeHistory;
import com.thefa.audit.model.entity.player.Player;
import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.audit.model.shared.Gender;
import com.thefa.common.dto.shared.ApiResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.thefa.audit.model.shared.DataSourceType.INTERNAL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@Transactional
public class GradeUploadTest extends AbstractIntegrationTest {

    @Autowired private PlayerController playerController;
    @Autowired private EntityManager entityManager;
    @Autowired private PlayerGradeHistoryRepository playerGradeHistoryRepository;

    @SpyBean
    private PlayerForeignMappingService playerForeignMappingService;

    @Test
    public void loadPlayerGradeTest() {
        Set<PlayerGradeUploadDTO> gradePayload = new HashSet<>();
        gradePayload.add(new PlayerGradeUploadDTO("11", "B1"));
        gradePayload.add(new PlayerGradeUploadDTO("222", "A1"));

        ApiResponse<Long> response = playerController.loadPlayersGrade(gradePayload);

        verify(playerForeignMappingService).findPlayersFanId(Mockito.any(DataSourceType.class), Mockito.anySet());

        assertEquals(Long.valueOf(2), response.getData());

        Player player = entityManager.find(Player.class, 1l);
        assertEquals("B1", player.getPlayerGrade());

        List<PlayerGradeHistory> gradeHistory = playerGradeHistoryRepository.findAllByFanId(player.getFanId());
        assertEquals(1, gradeHistory.size());
        assertEquals("B1", gradeHistory.get(0).getGrade());

        player = entityManager.find(Player.class, 22l);
        assertEquals("A1", player.getPlayerGrade());

        gradeHistory = playerGradeHistoryRepository.findAllByFanId(player.getFanId());
        assertEquals(2, gradeHistory.size());
        assertEquals("B1", gradeHistory.get(0).getGrade());

    }

    @Before
    public void setup() {
        createPlayer();
    }

    private PlayerDTO createPlayer() {
        CreatePlayerDTO player = new CreatePlayerDTO();
        player.setFanId(22l);
        player.setFirstName("Nayyer");
        player.setLastName("Kamran");
        player.setDateOfBirth(LocalDate.now());
        player.setGender(Gender.M);
        player.setPlayerGrade("B1");
        player.getForeignMappings().add(new PlayerForeignMappingDTO(INTERNAL, "222"));
        return playerController.savePlayerDetails(player).getData();
    }

    @After
    public void cleanup() {
        entityManager.createNativeQuery("Delete from fa_player_foreign_mapping where foreign_id = '222'");
        entityManager.createNativeQuery("Delete from fa_player where fan_id = 22");
    }
}
