package com.thefa.audit.model.dto.player.edit;

import com.thefa.audit.model.shared.SocialMediaType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class EditPlayerSocialDTO {

    private Long id;

    @NotNull
    private SocialMediaType socialMedia;

    @NotEmpty
    private String link;

}
