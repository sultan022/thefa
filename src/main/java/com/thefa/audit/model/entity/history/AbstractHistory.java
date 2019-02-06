package com.thefa.audit.model.entity.history;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
abstract class AbstractHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    @EqualsAndHashCode.Include
    private Long historyId;

    @Column(name = "player_id")
    protected String playerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
}
