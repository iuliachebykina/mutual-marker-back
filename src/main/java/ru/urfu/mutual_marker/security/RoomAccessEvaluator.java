package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.security.jwt.JwtAuthentication;
import ru.urfu.mutual_marker.service.profile.ProfileService;
import ru.urfu.mutual_marker.service.room.RoomService;

@Service
@Data
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomAccessEvaluator {
    RoomService roomService;
    ProfileService profileService;

    public boolean isMemberOfRoomById(Long roomId) {
        if(roomId == null){
            return false;
        }
        try {
            Room room = roomService.getRoomById(roomId);
            return checkRoomForProfile(room);
        } catch (Exception e){
            log.error("Failed to evaluate access to the room with id {}, room does not exist", roomId);
            return false;
        }

    }

    public boolean isMemberOfRoomByRoomCode(String roomCode){
        try {
            Room room = roomService.getRoomByCode(roomCode);
            return checkRoomForProfile(room);
        } catch (Exception e){
            log.error("Failed to evaluate access to room with code {}, room does not exist", roomCode);
            return false;
        }
    }

    private boolean checkRoomForProfile(Room room){
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            return false;
        }
        String username = authentication.getUsername();
        Profile checked = profileService.getProfileByEmail(username);

        if (checked == null){
            log.warn("Failed to find profile with principal {} to evaluate access", authentication.getName());
            return false;
        }
        return checked.getRooms().contains(room);
    }
}
