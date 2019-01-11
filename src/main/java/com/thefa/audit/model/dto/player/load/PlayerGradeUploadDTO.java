package com.thefa.audit.model.dto.player.load;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGradeUploadDTO {

    @NotEmpty
    String playerId;

    @NotEmpty
    @Length(max = 2)
    String Grade;
}
