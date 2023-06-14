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
import ru.urfu.mutual_marker.common.MarkMapper;
import ru.urfu.mutual_marker.dto.mark.AddMarkDto;
import ru.urfu.mutual_marker.dto.mark.MarkStepFeedbackDto;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.security.jwt.JwtAuthentication;
import ru.urfu.mutual_marker.service.exception.mark.MarkServiceException;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;
import ru.urfu.mutual_marker.service.mark.MarkService;
import ru.urfu.mutual_marker.service.mark.MarkStepFeedbackService;
import ru.urfu.mutual_marker.service.profile.ProfileService;
import ru.urfu.mutual_marker.service.project.ProjectService;

import java.time.LocalDateTime;
import java.util.Collections;

@ContextConfiguration(classes = {
        MarkService.class
})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
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
    MarkMapper markMapper;
    @MockBean
    MarkCalculator markCalculator;
    @MockBean
    MarkStepFeedbackService markStepFeedbackService;

    @Test
    public void when_add_mark_used_after_close_date_then_exception_thrown(){
        JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setAuthenticated(Boolean.TRUE);
        jwtAuthentication.setUsername("Test");
        jwtAuthentication.setFirstName("Test");
        Profile profile = Profile.builder().id(1L).build();

        Task task = Task.builder().id(1L).closeDate(LocalDateTime.MIN).build();
        MarkStepFeedbackDto markStep1 = new MarkStepFeedbackDto();
        markStep1.setValue(1);
        AddMarkDto addMarkDto = new AddMarkDto();
        addMarkDto.setMarkStepFeedbackDtos(Collections.singletonList(markStep1));
        addMarkDto.setProjectId(1L);
        Project project = Project.builder().id(1L).task(task).build();
        Mockito.when(profileService.getProfileByEmail(Mockito.anyString())).thenReturn(profile);
        Mockito.when(projectService.findProjectById(Mockito.anyLong())).thenReturn(project);
        Assertions.assertThrows(MarkServiceException.class, () -> markService.addMark(jwtAuthentication, addMarkDto, Boolean.FALSE, 1d));

    }
}
