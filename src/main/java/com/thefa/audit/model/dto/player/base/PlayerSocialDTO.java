package com.thefa.audit.model.dto.player.base;

import com.thefa.audit.model.shared.SocialMediaType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerSocialDTO {

    @EqualsAndHashCode.Include
    private Long id;

    private SocialMediaType socialMedia;

    private String link;

    private ZonedDateTime createdAt;

}
