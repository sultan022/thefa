/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thefa.audit.model.entity.player;

import com.thefa.audit.model.shared.AttachmentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Nawaz Haider
 */

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "fa_player_attachment")
public class PlayerAttachment {

    @Id
    @Column(name = "attachment_id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    @Column(name = "player_id")
    private String playerId;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type")
    private AttachmentType attachmentType;

    @Column(name = "camp_date")
    @ToString.Include
    @Temporal(TemporalType.DATE)
    private Date campDate;

    @Column(name = "uploaded_by")
    @CreatedBy
    private String uploadedBy;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    @CreationTimestamp
    private ZonedDateTime uploadedAt = ZonedDateTime.now();

}
