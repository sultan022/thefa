package com.thefa.audit.model.dto.player.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data @NoArgsConstructor @AllArgsConstructor
public class PlayerGradeDTO {

    @NotEmpty
    private String grade;

    private String description;
}
