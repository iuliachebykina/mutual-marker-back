package ru.urfu.mutual_marker.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.AttachmentDto;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.AttachmentRepository;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
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
    TaskRepository taskRepository;

    @Transactional
    public List<String> uploadAttachments(UserDetails principal, AttachmentDto[] attachmentDtos) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var filenames = new ArrayList<String>();
        for (var attachmentDto : attachmentDtos) {
            var filename = generateFilename(Objects.requireNonNull(attachmentDto.getFile().getOriginalFilename()));
            filenames.add(filename);
            var attachment = Attachment.builder()
                    .fileName(filename)
                    .description(attachmentDto.getDescription())
                    .contentType(attachmentDto.getFile().getContentType())
                    .student(profile.get())
                    .build();
            fileStorageService.save(attachmentDto.getFile(), filename);
            attachmentRepository.save(attachment);
        }
        return filenames;
    }

    @Transactional
    public void deleteAttachment(UserDetails principal, String filename) {
        Optional<Attachment> attachment = attachmentRepository.findByFileName(filename);
        if(attachment.isEmpty() || !attachment.get().getStudent().getEmail().equals(principal.getUsername())){
            return;
        }
        attachmentRepository.deleteByFileName(filename);
        fileStorageService.deleteAll(List.of(filename));

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
    public void appendAttachmentsToProject(UserDetails principal, AttachmentDto[] attachmentDtos, Long projectId) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project was not found."));

        if (!project.getStudent().equals(profile.get())) {
            throw new IllegalArgumentException("This user don't have access to this project");
        }

        for (var attachmentDto : attachmentDtos) {
            var filename = generateFilename(Objects.requireNonNull(attachmentDto.getFile().getOriginalFilename()));
            var projects = new HashSet<Project>();
            projects.add(project);
            var attachment = Attachment.builder()
                    .fileName(filename)
                    .contentType(attachmentDto.getFile().getContentType())
                    .student(profile.get())
                    .projects(projects)
                    .build();
            fileStorageService.save(attachmentDto.getFile(), filename);
            attachmentRepository.save(attachment);
            project.getAttachments().add(attachment);
        }
    }

    @Transactional
    public void appendAttachmentsToTask(UserDetails principal, AttachmentDto[] attachmentDtos, Long taskId){
        Profile profile = profileRepository.findByEmail(principal.getUsername()).orElse(null);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task was not found."));

        for (var attachmentDto : attachmentDtos) {
            var filename = generateFilename(Objects.requireNonNull(attachmentDto.getFile().getOriginalFilename()));
            var attachment = Attachment.builder()
                    .fileName(filename)
                    .contentType(attachmentDto.getFile().getContentType())
                    .student(profile)
                    .task(task)
                    .build();
            task.addAttachment(attachment);
            fileStorageService.save(attachmentDto.getFile(), filename);
            attachmentRepository.save(attachment);
        }
        taskRepository.save(task);
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

    public String getDescription(String filename) {
        return attachmentRepository.findByFileName(filename).orElseThrow(() -> new NotFoundException("File was not found.")).getDescription();
    }
}
