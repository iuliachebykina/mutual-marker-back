package ru.urfu.mutual_marker.common;

import org.mapstruct.*;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
            @Mapping(target = "name.firstName", source = "firstName"),
            @Mapping(target = "name.lastName", source = "lastName"),
            @Mapping(target = "name.patronymic", source = "patronymic")
    })
    Profile registrationInfoToProfileEntity(RegistrationInfo registrationInfo);

    @Mappings({
            @Mapping(target = "name.firstName", source = "name.firstName"),
            @Mapping(target = "name.lastName", source = "name.lastName"),
            @Mapping(target = "name.patronymic", source = "name.patronymic"),
            @Mapping(target = "password", ignore = true)
    })
    Profile updateProfile( Profile updatedProfile, @MappingTarget Profile oldProfile);
}
