package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.mutual_marker.dto.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.service.MarkService;

@RequestMapping("/marks")
@RestController
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarksApi {
    MarkService markService;

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
    @GetMapping("/getMarkForProject/{id}")
    public Mark getStudentMarkForProject(@PathVariable Long projectId, @PathVariable Long studentId){
        return markService.findMarkByProjectAndStudentIds(projectId, studentId);
    }

    @Secured({"ROLE_ADMIN", "ROLE_TEACHER"})
    @GetMapping("/deleteMark/{projectId}")
    public boolean deleteMarkForProject(@PathVariable Long projectId, @PathVariable Long studentId){
        try{
            markService.deleteMarkOnProjectForStudent(projectId, studentId);
        } catch (RuntimeException e){ //TODO Change to custom exception
            log.error("Failed to delete mark for project with id {}", projectId);
            return false;
        }
        return true;
    }

    public void addMarkStepsForProject(@PathVariable AddMarkStepDto addMarkStepDto){

    }
}
