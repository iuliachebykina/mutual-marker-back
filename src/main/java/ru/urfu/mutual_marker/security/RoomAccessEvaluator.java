package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
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

    public boolean isMemberOfRoom(Long roomId, Authentication authentication) {
        Room room = roomService.getRoomById(roomId);
        Profile checked = profileService.getProfileByEmail(authentication.getPrincipal().toString());
        return checked.getRooms().contains(room);
    }
}
