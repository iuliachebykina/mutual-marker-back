package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.urfu.mutual_marker.jpa.entity.Attachment;

import java.util.Optional;
import java.util.Set;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @Query(value = "select a from Attachment a where a.fileName in ?1")
    Set<Attachment> findAllByFileNamesAndDeletedIsFalse(Iterable<String> filenames);

    Optional<Attachment> findByFileNameAndDeletedIsFalse(String filename);

    void deleteByFileName(String filename);
}
