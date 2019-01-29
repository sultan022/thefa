package com.thefa.audit.model.dto.player.base;

import com.thefa.audit.model.shared.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
public class PlayerAttachmentPathDTO {

    private String path;

    private AttachmentType attachmentType;

    private LocalDate campDate;

    private String uploadedBy;

    private ZonedDateTime uploadedAt;
}
