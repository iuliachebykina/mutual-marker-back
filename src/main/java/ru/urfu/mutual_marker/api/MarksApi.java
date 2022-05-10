package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.NumberOfGraded;
import ru.urfu.mutual_marker.service.MarkService;
import ru.urfu.mutual_marker.service.MarkStepService;
import ru.urfu.mutual_marker.service.NumberOfGradedService;

import java.util.List;

@RequestMapping("api/marks")
@RestController
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarksApi {
    MarkService markService;
    MarkStepService markStepService;
    NumberOfGradedService numberOfGradedService;

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_STUDENT' or 'ROLE_TEACHER')")
    @GetMapping("/{projectId}/{studentId}")
    public ResponseEntity<Mark> getStudentMarkForProject(@PathVariable Long projectId, @PathVariable Long studentId) {

        Mark mark = markService.findMarkByProjectAndStudentIds(projectId, studentId);
        return new ResponseEntity<>(mark, HttpStatus.OK);

    }
    
    @GetMapping(value = "/numberOfGraded", params = { "page", "size" })
    public List<NumberOfGraded> getAllNumbersOfGraded(@RequestParam("page") int page,
                                                      @RequestParam("size") int size,
                                                      @CurrentSecurityContext(expression = "authentication.principal.username") String email){
        Pageable pageable = PageRequest.of(page, size);
        return numberOfGradedService.getAllNumbers(email, pageable);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @DeleteMapping("/{projectId}/{studentId}")
    public ResponseEntity<Mark> deleteMarkForProject(@PathVariable Long projectId, @PathVariable Long studentId) {

        Mark deleted = markService.deleteMarkOnProjectForStudent(projectId, studentId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @PostMapping("/markSteps")
    public ResponseEntity<List<MarkStep>> addMarkStepsForProject(@RequestBody List<AddMarkStepDto> addMarkStepDtoList){
        return new ResponseEntity<>(markStepService.addMarkSteps(addMarkStepDtoList), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @PostMapping("/markStepExisting")
    public ResponseEntity<MarkStep> addMarkStepForExistingProject(@RequestBody AddMarkStepDto addMarkStepDto){
        return new ResponseEntity<>(markStepService.addMarkStep(addMarkStepDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @PutMapping("/markStep")
    public ResponseEntity<MarkStep> updateMarkStep(@RequestBody MarkStep markStep){
        return new ResponseEntity<>(markStepService.updateMarkStep(markStep), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN' or 'ROLE_TEACHER')")
    @DeleteMapping("/{markStepId}")
    public ResponseEntity<MarkStep> deleteMarkStep(@PathVariable Long markStepId) {

        MarkStep deleted = markStepService.deleteMarkStep(markStepId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }
}
