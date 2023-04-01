package ru.urfu.mutual_marker.service.mark;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.mark.AddMarkDto;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.MarkStepFeedback;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.repository.MarkStepFeedbackRepository;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarkStepFeedbackService {
    final MarkStepFeedbackRepository markStepFeedbackRepository;
    final MarkStepRepository markStepRepository;
    final ProfileRepository profileRepository;

    @Transactional
    public void addMarkStepFeedbacksForMark(AddMarkDto addMarkDto){
        if (addMarkDto.getMarkStepDtos().isEmpty()){
            return;
        }
        List<MarkStepFeedback> feedbacks = new ArrayList<>();
        addMarkDto.getMarkStepDtos().forEach(ms -> {
            MarkStep markStep = markStepRepository.findById(ms.getMarkStepId())
                    .orElseThrow(() -> new RuntimeException(String.format("[MarkStepFeedbackService] Не удалось найти MarkStep с id %s", ms.getMarkStepId())));
            Profile owner = profileRepository.findById(ms.getOwnerId())
                    .orElseThrow(() -> new RuntimeException(String.format("[MarkStepFeedbackService] Не удалось найти Profile с id %s", ms.getOwnerId())));
            MarkStepFeedback markStepFeedback = MarkStepFeedback
                    .builder()
                    .markStep(markStep)
                    .owner(owner)
                    .comment(ms.getComment())
                    .value(ms.getValue())
                    .build();
            owner.addMarkStepFeedback(markStepFeedback);
            feedbacks.add(markStepFeedback);
        });
        markStepFeedbackRepository.saveAll(feedbacks);
    }
}
