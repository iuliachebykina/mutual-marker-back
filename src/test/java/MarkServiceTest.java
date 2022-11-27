import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import ru.urfu.mutual_marker.dto.AddMarkDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.NumberOfGradedRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.service.MarkService;
import ru.urfu.mutual_marker.service.ProfileService;
import ru.urfu.mutual_marker.service.ProjectService;
import ru.urfu.mutual_marker.service.TaskService;
import ru.urfu.mutual_marker.service.exception.MarkServiceException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = {MarkService.class})
public class MarkServiceTest {
    @Autowired
    MarkService markService;

    @MockBean
    MarkRepository markRepository;

    @MockBean
    ProjectRepository projectRepository;

    @MockBean
    ProfileService profileService;

    @MockBean
    ProjectService projectService;

    @MockBean
    TaskService taskService;

    @MockBean
    NumberOfGradedRepository numberOfGradedRepository;

    @Captor
    ArgumentCaptor<Mark> markCaptor;

    @Test
    public void when_markStepValue_isEmpty_then_exception_thrown(){
        AddMarkDto addMarkDto = new AddMarkDto();
        addMarkDto.setMarkStepValues(new ArrayList<>());
        addMarkDto.setProfileId(1L);
        addMarkDto.setProjectId(1L);

        Assertions.assertThrows(MarkServiceException.class, () -> markService.addStudentMark(addMarkDto));
    }

    @Test
    public void when_addMarkStudent_called_then_mark_correctly_calculated(){
        AddMarkDto addMarkDto = new AddMarkDto();
        addMarkDto.setMarkStepValues(Arrays.asList(1, 3, 5));
        addMarkDto.setProfileId(1L);
        addMarkDto.setProjectId(1L);

        Profile profile = Profile.builder().id(1L).build();
        Project project = Project.builder().id(1L).build();
        Mockito.when(profileService.findById(1L)).thenReturn(profile);
        Mockito.when(projectService.findProjectById(1L)).thenReturn(project);

        markService.addStudentMark(addMarkDto);

        Mockito.verify(markRepository).save(markCaptor.capture());
        Mark mark = markCaptor.getValue();

        Assertions.assertEquals(mark.getMarkValue(), 3);
    }

    @Test
    public void when_calculateMarkForProject_called_and_task_is_not_found_then_exception_thrown(){
        Project project = Project.builder().id(1L).task(null).build();
        Mockito.when(projectService.findProjectById(1L)).thenReturn(project);

        Assertions.assertThrows(MarkServiceException.class, () -> markService.calculateMarkForProject(1L, 1L, 1));
    }

    @Test
    public void when_calculateMarkForProject_called_without_min_numberOfGraded_reached_then_res_is_nan(){
        Task task = Task.builder().id(1L).minNumberOfGraded(5).build();
        Project project = Project.builder().id(1L).task(task).build();
        task.addProject(project);

        Mockito.when(projectService.findProjectById(1L)).thenReturn(project);
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(anyLong(), anyLong())).thenReturn(3L);

        Double actual = markService.calculateMarkForProject(1L, 1L, 1);

        Assertions.assertEquals(Double.NaN, actual);
    }

    @Test
    public void when_calculateMarkForProject_called_then_mark_returned_correctly(){
        Task task = Task.builder().id(1L).minNumberOfGraded(2).build();
        Project project = Project.builder().id(1L).task(task).build();
        task.addProject(project);
        Mark mark1 = Mark.builder().id(1L).markValue(5).build();
        Mark mark2 = Mark.builder().id(2L).markValue(12).build();
        project.addMark(mark1);
        project.addMark(mark2);

        Mockito.when(projectService.findProjectById(1L)).thenReturn(project);
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(anyLong(), anyLong())).thenReturn(2L);


        Double actual = markService.calculateMarkForProject(1L, 1L, 1);
        Assertions.assertEquals(8.5, actual);
    }

    @Test
    public void when_calculateMarkForProject_called_with_teacherMark_then_mark_returned_correctly(){
        Task task = Task.builder().id(1L).minNumberOfGraded(2).build();
        Project project = Project.builder().id(1L).task(task).build();
        task.addProject(project);
        Mark mark1 = Mark.builder().id(1L).markValue(5).isTeacherMark(Boolean.TRUE).coefficient(1d).build();
        Mark mark2 = Mark.builder().id(2L).markValue(12).build();
        project.addMark(mark1);
        project.addMark(mark2);

        Mockito.when(projectService.findProjectById(1L)).thenReturn(project);
        Mockito.when(markRepository.countAllByOwnerIdAndProjectTaskId(anyLong(), anyLong())).thenReturn(2L);


        Double actual = markService.calculateMarkForProject(1L, 1L, 1);
        Assertions.assertEquals(5, actual);
    }
}
