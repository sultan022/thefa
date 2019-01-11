package com.thefa.audit.model.dto.player.create;

import com.thefa.audit.model.shared.IntelType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class CreatePlayerIntelDTO {

    @NotNull
    private IntelType intelType;

    @Length(max = 1000)
    private String note;

}
