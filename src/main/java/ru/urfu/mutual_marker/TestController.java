package ru.urfu.mutual_marker;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.mutual_marker.dto.registration.RegistrationInfo;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepRepository;
import ru.urfu.mutual_marker.security.exception.UserExistingException;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.*;
import ru.urfu.mutual_marker.service.ProfileService;

import javax.annotation.security.PermitAll;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@PermitAll
public class TestController {

    AttachmentRepository attachmentRepository;
    MarkRepository markRepository;
    MarkStepRepository markStepRepository;
    ProfileRepository profileRepository;
    ProjectRepository projectRepository;
    RoomRepository roomRepository;
    TaskRepository taskRepository;


    ProfileService profileService;

    Random rand = new Random();

    @GetMapping("/test")
    @PermitAll
    String  test() throws UserExistingException {
        addProfiles();
        addRooms();
        addTasks();
 //       addProjects();

        return "ok";
    }




    @Transactional
    void addProfiles() throws UserExistingException {

        for (int i = 0; i < 100; i++) {
            String psqqword = generateString(10);
            String email = psqqword + "@mail.com";
            int roleID = rand.nextInt() % 3;
            Role role = Role.values()[roleID < 0? -1 * roleID: roleID ];
            String firstName = generateString(10);
            String lastName = generateString(10);

            RegistrationInfo registrationInfo = RegistrationInfo.builder()
                    .password(psqqword)
                    .firstName(firstName)
                    .email(email)
                    .lastName(lastName)
                    .build();

             profileService.saveProfile(registrationInfo, role);
        }

    }

    @Transactional
    void addRooms(){
        for (int i = 0; i < 100; i++) {
            String title = generateString(10);
            int teachersCount = 3;
            List<Profile> teachers = profileRepository
                    .findAllByRole(Role.ROLE_TEACHER,  PageRequest.of(0, 10))
                    .stream()
                    .limit(teachersCount)
                    .collect(Collectors.toList());

            int studentsCount =10;
            List<Profile> students = profileRepository
                    .findAllByRole(Role.ROLE_STUDENT,  PageRequest.of(0, 10))
                    .stream()
                    .limit(studentsCount)
                    .collect(Collectors.toList());

            String code = generateString(8);
            String description = generateString(50);


            Room room = Room.builder()
                    .title(title)
                    .description(description)
                    .code(code)
                    .deleted(false)
                    .teachers(new HashSet<>())
                    .students(new HashSet<>())
                    .build();





         //   room.addTask(taskRepository.findAll().get(0));

            for (Profile t:
                    students) {
                if(t != null)
                    room.addStudent(t);

            }

            for (Profile t:
                 teachers) {
                if(t != null)
                    room.addTeacher(t);

            }


            roomRepository.save(room);

        }
    }


    @Transactional
    void addTasks() {
        for (int i = 0; i < 30; i++) {
            long roomId = rand.nextLong() % roomRepository.count();
            if(roomId == 0)
                roomId = 1;
            Room room = roomRepository.findAll().get((int) (roomId < 0 ? -1* roomId : roomId));
            for (int j = 0; j < 40; j++) {
                String title = generateString(10);
                String description = generateString(70);
                LocalDateTime openDate = LocalDateTime.now();
                LocalDateTime closeDate = LocalDateTime.of(2022, 7, 5, 3, 56);

                Boolean deleted = false;


                Task task = Task.builder()
                        .title(title)
                        .description(description)
                        .openDate(openDate)
                        .closeDate(closeDate)
                        .room(room)
                        .deleted(deleted)
                        .minNumberOfGraded(4)
                        .build();
                taskRepository.save(task);
                room.addTask(task);
                roomRepository.save(room);

            }
        }

    }

    @Transactional
    void addProjects() {
        for (int i = 0; i < 40; i++) {


            long taskId = rand.nextLong() % taskRepository.count();
            Task task = taskRepository.findAll().get(getPositiveNumber(taskId));

            for (int j = 0; j < 60; j++) {
                Room room = task.getRoom();

                long studentId = rand.nextInt() % room.getStudents().size();
                Profile student = room.getStudents().iterator().next();

                String title = generateString(10);

                String description = generateString(40);
                Boolean deleted = false;

                Project project = Project.builder()
                        .task(task)
                        .student(student)
                        .title(title)
                        .description(description)
                        .deleted(deleted)
                        .build();
                projectRepository.save(project);
            }


        }
    }

    int getPositiveNumber(long i ){
       return (int) (i < 0 ? -1* i : i);
    }



    String generateString( int top){
        char data;
        StringBuilder dat = new StringBuilder();

        for (int i=0; i<=top; i++) {
            data = (char)(rand.nextInt(25)+97);
            dat.insert(0, data);
        }
        return dat.toString();

    }
}
