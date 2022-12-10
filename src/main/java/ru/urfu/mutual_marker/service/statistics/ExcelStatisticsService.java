package ru.urfu.mutual_marker.service.statistics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.service.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        XSSFWorkbook workbook = new XSSFWorkbook();
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null){
            throw new IllegalArgumentException("Task with given id not found");
        }

        Set<Project> allTaskProjects = task.getProjects();
        Sheet sheet = workbook.createSheet(task.getTitle());
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 8000);
        sheet.setColumnWidth(3, 6000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 18);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell nameCell = header.createCell(0);
        nameCell.setCellValue("Проект");
        nameCell.setCellStyle(headerStyle);
        Cell initialsHeader = header.createCell(1);
        initialsHeader.setCellValue("Группа");
        initialsHeader.setCellStyle(headerStyle);
        Cell groupHeader = header.createCell(2);
        groupHeader.setCellValue("ФИО");
        groupHeader.setCellStyle(headerStyle);
        Cell markHeader = header.createCell(3);
        markHeader.setCellValue("Оценка");
        markHeader.setCellStyle(headerStyle);

        int i = 1;
        for (Project project : allTaskProjects){
            Profile student = project.getStudent();

            CellStyle statsStyle = workbook.createCellStyle();
            XSSFFont statsFont = workbook.createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 14);
            font.setBold(false);
            statsStyle.setFont(statsFont);

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
                if (calculatedMark.isNaN()){
                    mark.setBlank();
                } else {
                    mark.setCellValue(calculatedMark);
                }
            } catch (Exception e){
                log.error("Error while filling excel for project. Project: {}", project);
                mark.setBlank();
            }
            i++;
        }

        //For local tests

//        File currDir = new File(".");
//        String path = currDir.getAbsolutePath();
//        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";
//        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            workbook.write(baos);
            workbook.close();
        } catch (Exception e){
            log.error("Error while writing statistics to excel ", e);
            throw new StatisticsServiceException("Failed to generate excel report", e);
        }
        String filename = String.format("Отчет по заданию \"%s\".xlsx", task.getTitle());
        ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentLength(resource.contentLength())
                .body(resource);
    }
}
