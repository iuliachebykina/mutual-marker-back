package ru.urfu.mutual_marker.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${mutual-marker.filepath}")
    private String filepath;

    private Path root;

    AmazonS3 client;

    @Override
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
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());
        try {
//            Files.copy(file.getInputStream(), this.root.resolve(filename));
            client.putObject("bucket", "key", file.getInputStream(), data);
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

//    @Override
//    public Resource load(String filename) {
//        try {
//            Path file = root.resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                throw new RuntimeException("Could not read the file!");
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("Error: " + e.getMessage());
//        }
//    }

    @Override
    public Resource load(String key) {
        S3Object object = client.getObject("bucket", key);
        Resource resource = new InputStreamResource(object.getObjectContent());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }

    @Override
    public void deleteAll(List<String> filenames) {
        try {
            filenames.forEach(filename -> {
                client.deleteObject("bucket", filename);
            });
        } catch (Exception e){
            log.error("Failed to delete files", e);
            throw new RuntimeException("Error when deleting files from object storage", e);
        }
    }
}
