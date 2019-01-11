package com.thefa.audit.model.table.the_fa_raw;

import com.thefa.audit.model.dto.player.base.*;
import com.thefa.audit.model.dto.rerference.ClubDTO;
import com.thefa.audit.model.shared.DataSourceType;
import com.thefa.audit.model.shared.Gender;
import com.thefa.audit.model.shared.SocialMediaType;
import com.thefa.audit.model.shared.SquadType;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FndPlayersTableTest {

    @Test
    public void givenPlayerDTO_whenBuildFndPlayerTable_thenBuildCorrectPlayersTableObj() {

        PlayerDTO playerDTO = buildPlayerDTO();

        FndPlayersTable fndPlayersTable = FndPlayersTable.fromPlayerDTO(playerDTO);

        assertEquals("Incorrect PlayerId", "11000", fndPlayersTable.getPlayerId());
        assertEquals("Incorrect FullName", "Omer X Arshad", fndPlayersTable.getFullName());
        assertEquals("Incorrect Firstname", "Omer", fndPlayersTable.getFirstName());
        assertEquals("Incorrect Lastname", "Arshad", fndPlayersTable.getLastName());
        assertEquals("Incorrect Nickname", "famous", fndPlayersTable.getNickName());
        assertEquals("Incorrect Date of Birth", "1970-01-01", fndPlayersTable.getDateOfBirth());
        assertEquals("Incorrect Gender", "male", fndPlayersTable.getGender());
        assertEquals("Incorrect Profile Image", "http://dummy.com/image.jpg", fndPlayersTable.getPlayerPhotoURL());
        assertEquals("Incorrect Nationality", "England", fndPlayersTable.getNationality());
        assertEquals("Incorrect Other Nation", "IRL", fndPlayersTable.getOtherEligibileNation());
        assertEquals("Incorrect Other Nation 2", "PAK", fndPlayersTable.getOtherEligibileNation2());
        assertEquals("Incorrect Primary Position", Integer.valueOf(5), fndPlayersTable.getPrimaryPositionNumber());
        assertEquals("Incorrect Second Position", Integer.valueOf(4), fndPlayersTable.getSecondPositionNumber());
        assertEquals("Incorrect Third Position", Integer.valueOf(3), fndPlayersTable.getThirdPostitionNumber());
        assertEquals("Incorrect England Squad Id", "SENIORS", fndPlayersTable.getEnglandSquadId());
        assertEquals("Incorrect Age Category", "SENIORS", fndPlayersTable.getAgeCategory());
        assertEquals("Incorrect Club Id", "club001", fndPlayersTable.getCurrentClubId());
        assertEquals("Incorrect Club Name", "Manchester United", fndPlayersTable.getCurrentClubName());
        assertEquals("Incorrect Facebook Id", "http://linke2.com", fndPlayersTable.getFacebookId());

    }

    private PlayerDTO buildPlayerDTO() {

        PlayerDTO playerDTO = new PlayerDTO();

        PlayerForeignMappingDTO internalMapping = new PlayerForeignMappingDTO();
        internalMapping.setForeignPlayerId("fapl00011000");
        internalMapping.setSource(DataSourceType.INTERNAL);
        playerDTO.getForeignMappings().add(internalMapping);

        playerDTO.setFirstName("Omer");
        playerDTO.setMiddleName("X");
        playerDTO.setLastName("Arshad");
        playerDTO.setKnownName("famous");

        playerDTO.setDateOfBirth(LocalDate.parse("1970-01-01"));

        playerDTO.setGender(Gender.M);

        playerDTO.setProfileImage("http://dummy.com/image.jpg");

        playerDTO.getEligibilities().addAll(Arrays.asList("ENG", "PAK", "USA", "IRL"));

        PlayerPositionDTO primaryPosition = new PlayerPositionDTO();
        primaryPosition.setPositionOrder(1);
        primaryPosition.setPositionNumber(5);

        PlayerPositionDTO secondPosition = new PlayerPositionDTO();
        secondPosition.setPositionOrder(2);
        secondPosition.setPositionNumber(4);

        PlayerPositionDTO thirdPosition = new PlayerPositionDTO();
        thirdPosition.setPositionOrder(3);
        thirdPosition.setPositionNumber(3);

        playerDTO.getPlayerPositions().addAll(Arrays.asList(primaryPosition, secondPosition, thirdPosition));

        PlayerSquadDTO squad1 = new PlayerSquadDTO();
        squad1.setSquad(SquadType.SENIORS);

        PlayerSquadDTO squad2 = new PlayerSquadDTO();
        squad2.setSquad(SquadType.U21);

        playerDTO.getPlayerSquads().addAll(Arrays.asList(squad1, squad2));

        ClubDTO club = new ClubDTO();
        club.setId("club001");
        club.setName("Manchester United");
        playerDTO.setClub(club);

        PlayerSocialDTO facebook1 = new PlayerSocialDTO();
        facebook1.setId(1L);
        facebook1.setSocialMedia(SocialMediaType.FACEBOOK);
        facebook1.setLink("http://linke1.com");
        facebook1.setCreatedAt(ZonedDateTime.now().minusDays(1));

        PlayerSocialDTO facebook2 = new PlayerSocialDTO();
        facebook2.setId(2L);
        facebook2.setSocialMedia(SocialMediaType.FACEBOOK);
        facebook2.setLink("http://linke2.com");
        facebook2.setCreatedAt(ZonedDateTime.now());

        playerDTO.getPlayerSocials().addAll(Arrays.asList(facebook1, facebook2));

        return playerDTO;
    }

}