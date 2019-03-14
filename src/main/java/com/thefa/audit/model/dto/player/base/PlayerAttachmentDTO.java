package com.thefa.audit.model.dto.player.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thefa.audit.model.shared.AttachmentType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlayerAttachmentDTO {

    @EqualsAndHashCode.Include
    private Long attachmentId;

    private String playerId;

    private String attachmentPath;

    private AttachmentType attachmentType;

    private LocalDate campDate;

    private String uploadedBy;

    private ZonedDateTime uploadedAt;

    @JsonIgnore
    public String getFilename() {
        return Optional.ofNullable(attachmentPath)
                .map(p -> p.split("/"))
                .map(arr -> arr[arr.length - 1])
                .orElse(null);
    }
}
