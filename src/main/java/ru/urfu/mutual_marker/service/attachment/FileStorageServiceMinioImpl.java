package ru.urfu.mutual_marker.service.attachment;

import com.jlefebure.spring.boot.minio.MinioException;
import com.jlefebure.spring.boot.minio.MinioService;
import io.minio.ObjectStat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
@Primary
public class FileStorageServiceMinioImpl implements FileStorageService {

    MinioService minioService;

    @Override
    public void save(MultipartFile file, String filename) {

        Path path = Path.of(filename);
        try {
            minioService.upload(path, file.getInputStream(), file.getContentType());
            var metadata = minioService.getMetadata(path);
            log.info("this file {} storage in bucket: {} on date: {}", metadata.name(), metadata.bucketName(), metadata.createdTime());
        } catch (IOException | MinioException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    @Override
    public void delete(String filename) {
        Path path = Path.of(filename);
        ObjectStat metadata = null;
        try {
            metadata = minioService.getMetadata(path);
            minioService.remove(path);
        } catch (MinioException e) {
            log.error("[FileStorageServiceMinioImpl] Ошибка во время сохранения файла в объектное хранилище", e);
            throw new ObjectStorageException("Ошибка во время сохранения файла в объектное хранилище", e);
        }
        log.info("this file {} removed in bucket: {} on date: {}", metadata.name(), metadata.bucketName(), metadata.createdTime());
    }

    @Override
    public Resource load(String filename){
        Path path = Path.of(filename);

        try {
            //Not using try with resources because of ResourceHttpMessageConverter custom closing
            //https://stackoverflow.com/questions/48660011/how-to-handle-io-streams-in-spring-mvc
            InputStream inputStream = minioService.get(path);
            return new InputStreamResource(inputStream);
        } catch (MinioException e){
            log.error("[FileStorageServiceMinioImpl] Ошибка во время получения файла из объектного хранилища", e);
            throw new ObjectStorageException("Ошибка во время получения файла из объектного хранилища", e);
        }
    }

    @Override
    public void deleteAll(List<String> filenames) {
        filenames.forEach(this::delete);
    }

}