package ru.urfu.mutual_marker.service.attachment;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {

    void save(MultipartFile file, String filename);
    Resource load(String filename);
    void deleteAll(List<String> filenames);
    void delete(String filename);
}
