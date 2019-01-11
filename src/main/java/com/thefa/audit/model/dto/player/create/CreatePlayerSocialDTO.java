package com.thefa.audit.model.dto.player.create;

import com.thefa.audit.model.shared.SocialMediaType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreatePlayerSocialDTO {

    @NotNull
    private SocialMediaType socialMedia;

    @NotEmpty
    private String link;

}
