package service.mark;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.urfu.mutual_marker.jpa.entity.*;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;
import ru.urfu.mutual_marker.service.project.ProjectService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;

@ContextConfiguration(classes = {
        MarkCalculator.class
})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class MarkCalculatorTest {

    @Autowired
    MarkCalculator markCalculator;

    @MockBean
    ProjectRepository projectRepository;

    @MockBean
    ProjectService projectService;

    @MockBean
    MarkRepository markRepository;

    @Test
    public void when_numberOfMarkedByStudent_not_exceeded_limit_then_mark_is_not_calculated(){
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(0));
        Project dummy = Project.builder().build();
        dummy.setStudent(Profile.builder().id(1L).build());
        Task task = Task.builder().id(1L).minNumberOfGraded(5).build();

        Double actual = markCalculator.calculateBeforeCloseDate(dummy, task, 2);

        Assertions.assertEquals(Double.NaN, actual);
    }

    @Test
    public void when_calculateBeforeCloseDate_with_no_teacher_marks_called_then_mark_calculated_correctly(){
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(5));
        Mark mark1 = Mark.builder().markValue(30).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(10).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(30).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).build();
        Project project = Project.builder().marks(marks).build();
        project.setTask(task);
        project.setStudent(Profile.builder().id(1L).build());
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Double actual = markCalculator.calculateBeforeCloseDate(project, task, 2);

        Assertions.assertEquals(78, actual);
    }

    @Test
    public void when_task_close_date_is_after_project_completion_then_mark_calculated_correctly(){
        Mark mark1 = Mark.builder().markValue(30).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(10).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(30).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).closeDate(LocalDateTime.MAX).build();
        Project project = Project.builder().marks(marks).completionDate(LocalDateTime.MIN).build();
        project.setTask(task);
        project.setStudent(Profile.builder().id(1L).build());
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(5));
        Mockito.when(projectRepository.findByStudentIdAndTaskIdAndDeletedIsFalse(anyLong(), anyLong())).thenReturn(Optional.of(project));

        Double actual = markCalculator.calculateMarkForProjectByTask(1L, 1L, 2);

        Assertions.assertEquals(78, actual);
    }

    @Test
    public void when_task_close_date_is_before_project_completion_then_mark_not_calculated(){
        Mark mark1 = Mark.builder().markValue(30).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(40).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(20).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).closeDate(LocalDateTime.MIN).build();
        Project project = Project.builder().marks(marks).completionDate(LocalDateTime.MAX).build();
        project.setTask(task);
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(5));
        Mockito.when(projectRepository.findByStudentIdAndTaskIdAndDeletedIsFalse(anyLong(), anyLong())).thenReturn(Optional.of(project));

        Double actual = markCalculator.calculateMarkForProjectByTask(1L, 1L, 2);

        Assertions.assertEquals(Double.NaN, actual);
    }

    @Test
    public void when_calculating_after_closeDate_and_project_completion_before_closeDate_and_minNumberOfGraded_not_reached_then_mark_not_calculated(){
        Mark mark1 = Mark.builder().markValue(30).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(40).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(20).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).closeDate(LocalDateTime.now().minusDays(3)).build();
        Project project = Project.builder().marks(marks).completionDate(LocalDateTime.MIN).build();
        project.setTask(task);
        project.setStudent(Profile.builder().id(1L).build());
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(3));
        Mockito.when(projectRepository.findByStudentIdAndTaskIdAndDeletedIsFalse(anyLong(), anyLong())).thenReturn(Optional.of(project));

        Double actual = markCalculator.calculateMarkForProjectByTask(1L, 1L, 2);

        Assertions.assertEquals(Double.NaN, actual);
    }

    @Test
    public void when_calculating_before_closeDate_and_project_completion_before_closeDate_and_minNumberOfGraded_not_reached_then_mark_not_calculated(){
        Mark mark1 = Mark.builder().markValue(30).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(10).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(30).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).closeDate(LocalDateTime.now().plusDays(3)).build();
        Project project = Project.builder().marks(marks).completionDate(LocalDateTime.MIN).build();
        project.setTask(task);
        project.setStudent(Profile.builder().id(1L).build());
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(5));
        Mockito.when(projectService.findProjectById(anyLong())).thenReturn(project);

        Double actual = markCalculator.calculateMarkForProject(project, 2);

        Assertions.assertEquals(78, actual);
    }

    @Test
    public void when_teacher_mark_is_present_then_coefficient_is_applied(){
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(5));
        Mark mark1 = Mark.builder().markValue(30).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(10).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(30).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Mark teacherMark = Mark.builder().markValue(10).isTeacherMark(true).coefficient(1d).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3, teacherMark);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).build();
        Project project = Project.builder().task(task).marks(marks).build();
        project.setStudent(Profile.builder().id(1L).build());
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Double actual = markCalculator.calculateBeforeCloseDate(project, task, 2);

        Assertions.assertEquals(33, actual);
    }

    @Test
    public void when_teacher_coefficient_is_half_then_mark_calculated_correctly(){
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(Mockito.any(Long.class), Mockito.any(Long.class))).thenReturn(Long.valueOf(5));
        Mark mark1 = Mark.builder().markValue(10).isTeacherMark(false).build();
        Mark mark2 = Mark.builder().markValue(11).isTeacherMark(false).build();
        Mark mark3 = Mark.builder().markValue(14).isTeacherMark(false).build();
        MarkStepValue val1 = MarkStepValue.builder().value(10).build();
        MarkStepValue val2 = MarkStepValue.builder().value(15).build();
        MarkStepValue val3 = MarkStepValue.builder().value(15).build();
        MarkStep markStep1 = MarkStep.builder().values(Set.of(val1, val2)).build();
        MarkStep markStep2 = MarkStep.builder().values(Set.of(val3)).build();
        Mark teacherMark = Mark.builder().markValue(20).isTeacherMark(true).coefficient(0.5).build();
        Set<Mark> marks = Set.of(mark1, mark2, mark3, teacherMark);
        Task task = Task.builder().id(1L).minNumberOfGraded(5).build();
        Project project = Project.builder().task(task).marks(marks).build();
        Set<Project> projects = Set.of(project);
        task.setProjects(projects);
        project.setStudent(Profile.builder().id(1L).build());
        task.setMarkSteps(Set.of(markStep1, markStep2));
        markStep1.setTasks(Set.of(task));
        markStep2.setTasks(Set.of(task));

        Double actual = markCalculator.calculateBeforeCloseDate(project, task, 2);

        Assertions.assertEquals(53, actual);
    }
}
