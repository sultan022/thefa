
package com.thefa.audit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.thefa.audit.model.dto.player.load.PlayerStatusUploadDTO;
import com.thefa.audit.model.shared.MaturationStatus;
import com.thefa.audit.model.shared.VulnerabilityStatus;
import one.util.streamex.StreamEx;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonUtil {
    public static PlayerStatusUploadDTO getEditPlayerStatusDTOSample(String playerId, String maturationDate, MaturationStatus maturationStatus, String vulnerabilityDate, VulnerabilityStatus vulnerabilityStatus) {

        PlayerStatusUploadDTO dto = new PlayerStatusUploadDTO();
        dto.setPlayerId(playerId);
        if (maturationDate != null) {
            dto.setMaturationDate(LocalDate.parse(maturationDate));
        }
        dto.setMaturationStatus(maturationStatus);
        if (vulnerabilityDate != null) {
            dto.setVulnerabilityDate(LocalDate.parse(vulnerabilityDate));
        }
        dto.setVulnerabilityStatus(vulnerabilityStatus);
        return dto;
    }

    public static String getEditPlayerStatusDTOSampleJson(List list) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        String json = "";
        try {
            json = mapper.writeValueAsString(list);
        } catch (Exception ex) {

        }
        return json;
    }

}
