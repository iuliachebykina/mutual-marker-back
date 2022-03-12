package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.Profile;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
