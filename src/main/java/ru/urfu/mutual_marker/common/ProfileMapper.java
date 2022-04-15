package ru.urfu.mutual_marker.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.urfu.mutual_marker.dto.profile.Admin;
import ru.urfu.mutual_marker.dto.profile.Student;
import ru.urfu.mutual_marker.dto.profile.Teacher;
import ru.urfu.mutual_marker.jpa.entity.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mappings({
            @Mapping(target = "firstName", source = "name.firstName"),
            @Mapping(target = "lastName", source = "name.lastName"),
            @Mapping(target = "patronymic", source = "name.patronymic")
    })
    Admin profileEntityToAdminDto(Profile profile);



    @Mappings({
            @Mapping(target = "firstName", source = "name.firstName"),
            @Mapping(target = "lastName", source = "name.lastName"),
            @Mapping(target = "patronymic", source = "name.patronymic")
    })
    Teacher profileEntityToTeacherDto(Profile profile);

    @Mappings({
            @Mapping(target = "firstName", source = "name.firstName"),
            @Mapping(target = "lastName", source = "name.lastName"),
            @Mapping(target = "patronymic", source = "name.patronymic")
    })
    Student profileEntityToStudentDto(Profile profile);
}
