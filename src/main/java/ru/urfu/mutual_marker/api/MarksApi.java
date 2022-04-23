package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.service.MarkService;
import ru.urfu.mutual_marker.service.MarkStepService;
import ru.urfu.mutual_marker.service.exception.MarkServiceException;
import ru.urfu.mutual_marker.service.exception.MarkStepServiceException;

import java.util.List;

@RequestMapping("/marks")
@RestController
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarksApi {
    MarkService markService;
    MarkStepService markStepService;

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_STUDENT' or 'ROLE_TEACHER')")
    @GetMapping("/marks/{projectId}/{studentId}")
    public ResponseEntity<Mark> getStudentMarkForProject(@PathVariable Long projectId, @PathVariable Long studentId){
        try {
            Mark mark = markService.findMarkByProjectAndStudentIds(projectId, studentId);
            return new ResponseEntity<>(mark, HttpStatus.OK);
        } catch (MarkServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @GetMapping("/marks/delete/{projectId}/{studentId}")
    public ResponseEntity<Mark> deleteMarkForProject(@PathVariable Long projectId, @PathVariable Long studentId){
        try{
            Mark deleted = markService.deleteMarkOnProjectForStudent(projectId, studentId);
            return new ResponseEntity<>(deleted, HttpStatus.OK);
        } catch (MarkServiceException e){
            log.error("Failed to delete mark for project with id {}", projectId);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @PostMapping("marks/addMarkSteps")
    public ResponseEntity<List<MarkStep>> addMarkStepsForProject(@RequestBody List<AddMarkStepDto> addMarkStepDtoList){
        return new ResponseEntity<>(markStepService.addMarkSteps(addMarkStepDtoList), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @PostMapping("marks/addMarkStepExisting")
    public ResponseEntity<MarkStep> addMarkStepForExistingProject(@RequestBody AddMarkStepDto addMarkStepDto){
        return new ResponseEntity<>(markStepService.addMarkStep(addMarkStepDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @PutMapping("marks/updateMarkStep")
    public ResponseEntity<MarkStep> updateMarkStep(@RequestBody MarkStep markStep){
        return new ResponseEntity<>(markStepService.updateMarkStep(markStep), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @GetMapping("marks/deleteMarkStep/{markStepId}")
    public ResponseEntity<MarkStep> deleteMarkStep(@PathVariable Long markStepId){
        try{
            MarkStep deleted = markStepService.deleteMarkStep(markStepId);
            return new ResponseEntity<>(deleted, HttpStatus.OK);
        } catch (MarkStepServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
