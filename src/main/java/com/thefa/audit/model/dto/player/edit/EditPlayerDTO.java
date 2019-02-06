package com.thefa.audit.model.dto.player.edit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EditPlayerDTO {

    @EqualsAndHashCode.Include
    @NotNull
    private String playerId;

    @Valid
    private List<EditPlayerIntelDTO> playerIntels = new ArrayList<>();

}
