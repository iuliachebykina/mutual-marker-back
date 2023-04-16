package ru.urfu.mutual_marker.service.statistics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.service.mark.MarkService;
import ru.urfu.mutual_marker.service.statistics.anomaly.AnomalyDiscoveryService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsDtoService {
    final MarkService markService;
    final TaskRepository taskRepository;
    final AnomalyDiscoveryService anomalyDiscoveryService;

    public List<StatisticsDto> getStatistics(Long taskId){
        Task task = taskRepository.findById(taskId).orElse(null);
        List<StatisticsDto> res = new ArrayList<>();
        if (task == null){
            throw new IllegalArgumentException("Task with given id not found");
        }

        for (Project project : task.getProjects()){
            Profile student = project.getStudent();
            String initialsString = student.getName().toString();
            Double calculatedMark;
            try {
                calculatedMark = markService.calculateMarkForProject(project.getId(), student.getId(), 2);
            } catch (Exception e){
                log.error("Error while processing statistics for project with id {}", project.getId());
                continue;
            }
            Boolean anomalyDetected = anomalyDiscoveryService.kruskalWallisDetectAnomaly(project);
            StatisticsDto dto = StatisticsDto.builder()
                    .fullName(initialsString)
                    .group(student.getStudentGroup())
                    .mark(calculatedMark.toString())
                    .project(project.getTitle())
                    .projectId(project.getId())
                    .anomalyDetected(anomalyDetected)
                    .build();
            res.add(dto);
        }
        return res;
    }
}
