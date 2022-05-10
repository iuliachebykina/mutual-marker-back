package ru.urfu.mutual_marker.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.repository.AttachmentRepository;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AttachmentService {

    private final String FILENAME_PATTERN = "^.*\\.";

    AttachmentRepository attachmentRepository;
    ProfileRepository profileRepository;
    ProjectRepository projectRepository;
    FileStorageService fileStorageService;

    @Transactional
    public List<String> uploadAttachments(UserDetails principal, MultipartFile[] files) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var filenames = new ArrayList<String>();
        for (var file : files) {
            var filename = generateFilename(Objects.requireNonNull(file.getOriginalFilename()));
            filenames.add(filename);
            var attachment = Attachment.builder()
                    .fileName(filename)
                    .contentType(file.getContentType())
                    .student(profile.get())
                    .build();
            fileStorageService.save(file, filename);
            attachmentRepository.save(attachment);
        }
        return filenames;
    }

    public ResponseEntity downloadFile(String filename) {
        var attachment = attachmentRepository.findByFileName(filename).orElseThrow(() -> new NotFoundException("File was not found."));
        var file = fileStorageService.load(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, attachment.getContentType())
                .body(file);
    }

    @Transactional
    public void appendAttachments(UserDetails principal, MultipartFile[] files, Long projectId) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project was not found."));

        if (!project.getStudent().equals(profile.get())) {
            throw new IllegalArgumentException("This user don't have access to this project");
        }

        for (var file : files) {
            var filename = generateFilename(Objects.requireNonNull(file.getOriginalFilename()));
            var projects = new HashSet<Project>();
            projects.add(project);
            var attachment = Attachment.builder()
                    .fileName(filename)
                    .contentType(file.getContentType())
                    .student(profile.get())
                    .projects(projects)
                    .build();
            fileStorageService.save(file, filename);
            attachmentRepository.save(attachment);
            project.getAttachments().add(attachment);
        }
    }

    @Transactional
    public void deleteUseless() {
        var attachments = attachmentRepository.findAll()
                .stream().filter(attachment -> attachment.getProjects().isEmpty())
                .map(Attachment::getFileName)
                .collect(Collectors.toList());
        fileStorageService.deleteAll(attachments);
    }

    private String generateFilename(String oldName){
        return oldName.replaceAll(FILENAME_PATTERN, NanoIdUtils.randomNanoId() + '.');
    }
}
