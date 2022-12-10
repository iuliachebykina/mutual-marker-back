package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.service.statistics.ExcelStatisticsService;
import ru.urfu.mutual_marker.service.statistics.StatisticsDto;
import ru.urfu.mutual_marker.service.statistics.StatisticsDtoService;

import java.util.List;

@RestController
@RequestMapping("api/statistics")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsApi {
    ExcelStatisticsService excelStatisticsService;
    StatisticsDtoService statisticsDtoService;

    @Operation(summary = "Получение статистики Excel файлом")
    @PostMapping("/excel")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getExcelStatistics(@RequestParam("taskId") Long taskId) {
        return excelStatisticsService.statisticsForProject(taskId);
    }

    @Operation(summary = "Получение статистики в виде DTO для отображения на интерфейсе")
    @PostMapping("/dto")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<List<StatisticsDto>> getDtoStatistics(@RequestParam("taskId") Long taskId) {
        return new ResponseEntity<>(statisticsDtoService.getStatistics(taskId), HttpStatus.OK);
    }
}
