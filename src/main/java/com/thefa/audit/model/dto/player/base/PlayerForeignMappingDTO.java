package com.thefa.audit.model.dto.player.base;

import com.thefa.audit.model.shared.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class PlayerForeignMappingDTO {

    @EqualsAndHashCode.Include
    @NotNull
    private DataSourceType source;

    @NotEmpty
    private String foreignPlayerId;

}
