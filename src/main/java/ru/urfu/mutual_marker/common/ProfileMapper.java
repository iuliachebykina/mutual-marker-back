package ru.urfu.mutual_marker.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProfileMapper {
    @Mappings({
            @Mapping(target = "firstName", source = "name.firstName"),
            @Mapping(target = "lastName", source = "name.lastName"),
            @Mapping(target = "patronymic", source = "name.patronymic")
    })
    AdminInfo profileEntityToAdminDto(Profile profile);



    @Mappings({
            @Mapping(target = "firstName", source = "name.firstName"),
            @Mapping(target = "lastName", source = "name.lastName"),
            @Mapping(target = "patronymic", source = "name.patronymic")
    })
    TeacherInfo profileEntityToTeacherDto(Profile profile);

    @Mappings({
            @Mapping(target = "firstName", source = "name.firstName"),
            @Mapping(target = "lastName", source = "name.lastName"),
            @Mapping(target = "patronymic", source = "name.patronymic")
    })
    StudentInfo profileEntityToStudentDto(Profile profile);

    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "name.firstName", source = "firstName"),
            @Mapping(target = "name.lastName", source = "lastName"),
            @Mapping(target = "name.patronymic", source = "patronymic")



    })
    Profile registrationInfoToProfileEntity(RegistrationInfo registrationInfo);
}
