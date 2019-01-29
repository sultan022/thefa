package com.thefa.audit.model.dto.player.base;

import com.thefa.audit.model.shared.AttachmentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerAttachmentDTO {

    @EqualsAndHashCode.Include
    private Long attachmentId;

    private Long fanId;

    private String attachmentPath;

    private AttachmentType attachmentType;

    private LocalDate campDate;

    private String uploadedBy;

    private ZonedDateTime uploadedAt;
}
