package ru.urfu.mutual_marker.service.statistics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.service.*;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelStatisticsService {
    final MarkService markService;
    final TaskRepository taskRepository;

    public ResponseEntity<Resource> statisticsForProject(Long taskId) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(ResourceUtils.getFile("classpath:resources/statistics-template.xlsx"))) {
            Task task = taskRepository.findById(taskId).orElse(null);
            if (task == null) {
                throw new IllegalArgumentException("Task with given id not found");
            }

            Set<Project> allTaskProjects = task.getProjects();

            int i = 1;
            for (Project project : allTaskProjects) {
                Profile student = project.getStudent();

                CellStyle statsStyle = workbook.createCellStyle();
                XSSFFont statsFont = workbook.createFont();
                statsFont.setFontName("Times New Roman");
                statsFont.setFontHeightInPoints((short) 14);
                statsStyle.setFont(statsFont);
                Sheet sheet = workbook.getSheet("TaskName");

                Row stats = sheet.createRow(i);

                Cell projectName = stats.createCell(0);
                projectName.setCellStyle(statsStyle);
                projectName.setCellValue(project.getTitle());

                Cell group = stats.createCell(1);
                group.setCellStyle(statsStyle);
                group.setCellValue(project.getStudent().getStudentGroup());

                Cell initials = stats.createCell(2);
                String initialsString = student.getName().toString();
                initials.setCellStyle(statsStyle);
                initials.setCellValue(initialsString);

                Cell mark = stats.createCell(3);
                mark.setCellStyle(statsStyle);
                Double calculatedMark;
                try {
                    calculatedMark = markService.calculateMarkForProject(project.getId(), student.getId(), 2);
                    if (calculatedMark.isNaN()) {
                        mark.setBlank();
                    } else {
                        mark.setCellValue(calculatedMark);
                    }
                } catch (Exception e) {
                    log.error("Error while filling excel for project. Project: {}", project);
                    mark.setBlank();
                }
                i++;
            }

            //For local tests

//        File currDir = new File(".");
//        String path = currDir.getAbsolutePath();
//        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
//        try {
//            FileOutputStream outputStream = new FileOutputStream(fileLocation);
//            workbook.write(outputStream);
//            workbook.close();
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        return ResponseEntity.ok().build();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);

            String filename = String.format("Отчет по заданию \"%s\".xlsx", task.getTitle());
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());


            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException | InvalidFormatException e) {
            log.error("Error while trying to build excel report", e);
            throw new RuntimeException(e);
        }
    }
}
