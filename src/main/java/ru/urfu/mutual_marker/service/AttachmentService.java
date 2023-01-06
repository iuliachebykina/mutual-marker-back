package ru.urfu.mutual_marker.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.math3.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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
    public List<String> uploadAttachmentsAndReturnNames(UserDetails principal, MultipartFile[] files, AttachmentDto[] attachmentDtos) {

        return uploadAttachments(principal,files, attachmentDtos).stream().map(Attachment::getFileName).collect(Collectors.toList());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Attachment> uploadAttachments(UserDetails principal, MultipartFile[] files, AttachmentDto[] attachmentDtos) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var filenames = new ArrayList<Attachment>();
        List<Pair<MultipartFile, String>> fileToDescription = resolveFileToDescription(files, attachmentDtos);
        for (var pair : fileToDescription) {
//            var filename = generateFilename(Objects.requireNonNull(attachmentDto.getFile().getOriginalFilename()));
            MultipartFile file = pair.getFirst();
            var attachment = Attachment.builder()
                    .fileName(file.getName())
                    .description(pair.getSecond())
                    .contentType(file.getContentType())
                    .student(profile.get())
                    .build();
            fileStorageService.save(file, file.getName());
            Attachment toReturn = attachmentRepository.save(attachment); //TODO Naming
            filenames.add(toReturn);
        }
        return filenames;
    }

    private List<Pair<MultipartFile, String>> resolveFileToDescription(MultipartFile[] files, AttachmentDto[] attachmentDtos){
        List<Pair<MultipartFile, String>> res = new ArrayList<>();
        Arrays.stream(files).forEach(file -> {
            AttachmentDto desc = Arrays.stream(attachmentDtos).filter(dto -> dto.getFileName().equals(file.getName())).findFirst().orElseThrow(() -> new RuntimeException("Failed to find file"));
            Pair<MultipartFile, String> pair = Pair.create(file, desc.getDescription());
            res.add(pair);
        });
        return res;
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
    public void appendAttachmentsToProject(UserDetails principal, MultipartFile[] files, AttachmentDto[] attachmentDtos, Long projectId) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundException("Project was not found."));

        if (!project.getStudent().equals(profile.get())) {
            throw new IllegalArgumentException("This user don't have access to this project");
        }

        List<Pair<MultipartFile, String>> fileToDescription = resolveFileToDescription(files, attachmentDtos);

        for (var pair : fileToDescription) {
//            var filename = generateFilename(Objects.requireNonNull(attachmentDto.getFile().getOriginalFilename()));
            MultipartFile file = pair.getFirst();
            var projects = new HashSet<Project>();
            projects.add(project);
            var attachment = Attachment.builder()
                    .fileName(file.getName())
                    .contentType(file.getContentType())
                    .student(profile.get())
                    .projects(projects)
                    .build();
            fileStorageService.save(file, file.getName());
            attachmentRepository.save(attachment);
            project.getAttachments().add(attachment);
        }
    }

    @Transactional
    public void appendAttachmentsToTask(UserDetails principal, MultipartFile[] files, AttachmentDto[] attachmentDtos, Long taskId){
        Profile profile = profileRepository.findByEmail(principal.getUsername()).orElse(null);
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task was not found."));

        List<Pair<MultipartFile, String>> fileToDescription = resolveFileToDescription(files, attachmentDtos);

        for (var pair : fileToDescription) {
            MultipartFile file = pair.getFirst();
//            var filename = generateFilename(Objects.requireNonNull(attachmentDto.getFile().getOriginalFilename()));
            var attachment = Attachment.builder()
                    .fileName(file.getName())
                    .contentType(file.getContentType())
                    .student(profile)
                    .task(task)
                    .build();
            task.addAttachment(attachment);
            fileStorageService.save(file, file.getName());
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
