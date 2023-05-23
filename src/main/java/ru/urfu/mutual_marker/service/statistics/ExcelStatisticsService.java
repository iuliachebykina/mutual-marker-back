package ru.urfu.mutual_marker.service.statistics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.service.exception.statistics.StatisticsServiceException;
import ru.urfu.mutual_marker.service.mark.MarkService;
import ru.urfu.mutual_marker.service.project.ProjectService;
import ru.urfu.mutual_marker.service.statistics.anomaly.AnomalyDiscoveryService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExcelStatisticsService {
    final MarkService markService;
    final TaskRepository taskRepository;
    final ResourceLoader resourceLoader;
    final ProjectService projectService;

    final AnomalyDiscoveryService anomalyDiscoveryService;

    public ResponseEntity<Resource> statisticsForProject(Long projectId){
        Project project = projectService.findProjectById(projectId);
        return getStatisticsFile(project.getTitle(), Set.of(project));
    }

    public ResponseEntity<Resource> statisticsForTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            throw new IllegalArgumentException("Task with given id not found");
        }

        return getStatisticsFile(task.getTitle(), task.getProjects());
    }

    public ResponseEntity<Resource> getStatisticsFile(String title, Set<Project> projects){
        try (XSSFWorkbook workbook = new XSSFWorkbook(resourceLoader.getResource("classpath:/statistics-template.xlsx").getInputStream())) {


            CellStyle statsStyle = workbook.createCellStyle();
            XSSFFont statsFont = workbook.createFont();
            statsFont.setFontName("Times New Roman");
            statsFont.setFontHeightInPoints((short) 14);
            statsStyle.setFont(statsFont);
            Sheet sheet = workbook.createSheet(title);

            int i = 1;
            for (Project project : projects) {
                Profile student = project.getStudent();

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
                    calculatedMark = markService.calculateMarkForProject(project, 2);
                    if (calculatedMark.isNaN()) {
                        mark.setBlank();
                    } else {
                        mark.setCellValue(calculatedMark);
                    }
                } catch (Exception e) {
                    log.error("Error while filling excel for project. Project: {}", project);
                    mark.setBlank();
                }
                Boolean detectedAnomaly = anomalyDiscoveryService.kruskalWallisDetectAnomaly(project);
                Cell anomalyDetectedFlag = stats.createCell(4);
                if (detectedAnomaly){
                    anomalyDetectedFlag.setCellStyle(statsStyle);
                    anomalyDetectedFlag.setCellValue("Замечена аномалия оценивания!");
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

            String filename = String.format("Отчет по заданию \"%s\".xlsx", title);
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());


            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            log.error("Error while trying to build excel report", e);
            throw new StatisticsServiceException("Failed to build excel report", e);
        }
    }
}
