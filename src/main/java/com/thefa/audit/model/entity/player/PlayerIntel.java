package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.shared.IntelType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "fa_player_Intel")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerIntel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "player_id")
    private String playerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "intel_type")
    private IntelType intelType;

    @Column(name = "note")
    private String note;

    @Column(name = "archived")
    private boolean archived;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private ZonedDateTime updatedAt;

}
