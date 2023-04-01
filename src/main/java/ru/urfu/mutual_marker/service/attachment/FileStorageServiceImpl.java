package ru.urfu.mutual_marker.service.attachment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.service.exception.file.FileStorageException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${mutual-marker.filepath}")
    private String filepath;

    private Path root;

    @PostConstruct
    public void init() {
        File directory = new File(filepath);
        if (! directory.exists()){
            directory.mkdir();
        }
        root = directory.toPath();
    }

    @Override
    public void save(MultipartFile file, String filename) {
        try {
            Files.copy(file.getInputStream(), this.root.resolve(filename));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Failed to read file", e);
        }
    }

    @Override
    public void deleteAll(List<String> filenames) {
        filenames.forEach(this::delete);
    }

    @Override
    public void delete(String filename) {
        try {
            Files.deleteIfExists(root.resolve(filename));
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete file", e);
        }
    }
}
