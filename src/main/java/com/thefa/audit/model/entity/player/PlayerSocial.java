package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.shared.SocialMediaType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "fa_player_social")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerSocial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "player_id")
    private String playerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_media")
    private SocialMediaType socialMedia;

    @Column(name = "link")
    private String link;

    @Column(name = "created_at")
    @CreatedDate
    private ZonedDateTime createdAt;

}
