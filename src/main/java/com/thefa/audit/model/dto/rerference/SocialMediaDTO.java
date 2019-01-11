package com.thefa.audit.model.dto.rerference;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SocialMediaDTO {

    @EqualsAndHashCode.Include
    private String name;

    private String icon;

}
