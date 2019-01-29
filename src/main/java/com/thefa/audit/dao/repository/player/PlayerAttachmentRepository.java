package com.thefa.audit.dao.repository.player;

import com.thefa.audit.model.entity.player.PlayerAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PlayerAttachmentRepository extends JpaRepository<PlayerAttachment, Long> {

    Optional<PlayerAttachment> findByFanIdAndAttachmentId(Long fanId, Long attachmentId);

    Stream<PlayerAttachment> findAllByFanIdOrderByUploadedAtDesc(Long fanId);
}
