package com.thefa.audit.model.table.the_fa_raw;

import com.google.cloud.Timestamp;
import com.thefa.audit.model.dto.player.base.PlayerGradeDTO;
import com.thefa.audit.model.dto.player.small.PlayerBasicDTO;
import com.thefa.audit.model.shared.InjuryStatus;
import com.thefa.audit.model.shared.MaturationStatus;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Optional;

@Data
public class FndPlayerPpsDataTable {

    private String playerId;

    private String playerGrade;

    private String maturationDate;

    private String maturationStatus;

    private String maturationIndex;

    private String vulnerabilityDate;

    private Integer vulnerabilityStatus;

    private Integer vulnerabilityStatus4Weeks;

    private Integer vulnerabilityStatus8Weeks;

    private Integer vulnerabilityStatus12Weeks;

    private String injuryStatus;

    private String expectedReturnDate;

    private String lastModified;

    public static FndPlayerPpsDataTable fromPlayerDTO(@NonNull PlayerBasicDTO playerDTO,
                                                      String lastModified) {

        FndPlayerPpsDataTable fndPlayerPpsDataTable = new FndPlayerPpsDataTable();

        fndPlayerPpsDataTable.playerId = playerDTO.getPlayerId();
        fndPlayerPpsDataTable.playerGrade = Optional.ofNullable(playerDTO.getPlayerGrade()).map(PlayerGradeDTO::getGrade).orElse(null);
        fndPlayerPpsDataTable.maturationDate = Optional.ofNullable(playerDTO.getMaturationDate()).map(LocalDate::toString).orElse(null);
        fndPlayerPpsDataTable.maturationStatus = Optional.ofNullable(playerDTO.getMaturationStatus()).map(MaturationStatus::name).orElse(null);
        fndPlayerPpsDataTable.maturationIndex = Optional.ofNullable(playerDTO.getMaturationStatus()).map(MaturationStatus::colour).orElse(null);
        fndPlayerPpsDataTable.vulnerabilityDate = Optional.ofNullable(playerDTO.getVulnerabilityDate()).map(LocalDate::toString).orElse(null);
        fndPlayerPpsDataTable.vulnerabilityStatus = playerDTO.getVulnerabilityStatus();
        fndPlayerPpsDataTable.vulnerabilityStatus4Weeks = playerDTO.getVulnerabilityStatus4Weeks();
        fndPlayerPpsDataTable.vulnerabilityStatus8Weeks = playerDTO.getVulnerabilityStatus8Weeks();
        fndPlayerPpsDataTable.vulnerabilityStatus12Weeks = playerDTO.getVulnerabilityStatus12Weeks();
        fndPlayerPpsDataTable.injuryStatus = Optional.ofNullable(playerDTO.getInjuryStatus()).map(InjuryStatus::name).orElse(null);
        fndPlayerPpsDataTable.expectedReturnDate = Optional.ofNullable(playerDTO.getExpectedReturnDate()).map(LocalDate::toString).orElse(null);
        fndPlayerPpsDataTable.lastModified = Optional.ofNullable(lastModified).orElseGet(() -> Timestamp.now().toString());

        return fndPlayerPpsDataTable;
    }
}
