package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.service.ProfileService;
import ru.urfu.mutual_marker.service.RoomService;

@Service
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomAccessEvaluator {
    RoomService roomService;
    ProfileService profileService;

    public boolean isMemberOfRoom(Long roomId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Room room = roomService.getRoomById(roomId);
        UserDetails details = (UserDetails)authentication.getPrincipal();
        Profile checked = profileService.getProfileByEmail(details.getUsername());
        if (checked == null || room == null){
            return false;
        }
        return checked.getRooms().contains(room);
    }

    public boolean isMemberOfRoom(String roomCode){
        Room room = roomService.getRoomByCode(roomCode);
        return isMemberOfRoom(room.getId());
    }
}
