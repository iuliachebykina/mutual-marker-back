package ru.urfu.mutual_marker.service.mark;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.mark.AddMarkDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.MarkStepFeedback;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.repository.MarkStepFeedbackRepository;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarkStepFeedbackService {
    final MarkStepFeedbackRepository markStepFeedbackRepository;
    final MarkStepRepository markStepRepository;
    final ProfileRepository profileRepository;

    @Transactional
    public void addMarkStepFeedbacksForMark(String reviewerEmail, AddMarkDto addMarkDto, Mark mark){
        if (addMarkDto.getMarkStepFeedbackDtos().isEmpty()){
            return;
        }
        List<MarkStepFeedback> feedbacks = new ArrayList<>();
        addMarkDto.getMarkStepFeedbackDtos().forEach(ms -> {
            MarkStep markStep = markStepRepository.findById(ms.getMarkStepId())
                    .orElseThrow(() -> new RuntimeException(String.format("[MarkStepFeedbackService] Не удалось найти MarkStep с id %s", ms.getMarkStepId())));
            Profile owner = profileRepository.getByEmailAndDeletedIsFalse(reviewerEmail.toLowerCase(Locale.ROOT))
                    .orElseThrow(() -> new RuntimeException(String.format("[MarkStepFeedbackService] Не удалось найти Profile с id %s", ms.getReviewerId())));
            MarkStepFeedback markStepFeedback = MarkStepFeedback
                    .builder()
                    .markStep(markStep)
                    .mark(mark)
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
