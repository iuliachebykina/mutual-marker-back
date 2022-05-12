package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.service.ProfileService;
import ru.urfu.mutual_marker.service.RoomService;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

@Service
@Data
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomAccessEvaluator {
    RoomService roomService;
    ProfileService profileService;

    public boolean isMemberOfRoomById(Long roomId) {
        try {
            Room room = roomService.getRoomById(roomId);
            return checkRoomForProfile(room);
        } catch (RoomServiceException e){
            log.error("Failed to evaluate access to the room with id {}, room does not exist", roomId);
            return false;
        }

    }

    public boolean isMemberOfRoomByRoomCode(String roomCode){
        try {
            Room room = roomService.getRoomByCode(roomCode);
            return checkRoomForProfile(room);
        } catch (RoomServiceException e){
            log.error("Failed to evaluate access to room with code {}, room does not exist", roomCode);
            return false;
        }
    }

    private boolean checkRoomForProfile(Room room){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Profile checked = profileService.getProfileByEmail(authentication.getName());
        if (checked == null){
            log.warn("Failed to find profile with principal {} to evaluate access", authentication.getName());
            return false;
        }
        return checked.getRooms().contains(room);
    }
}
