package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.NumberOfGraded;
import ru.urfu.mutual_marker.jpa.repository.NumberOfGradedRepository;
import ru.urfu.mutual_marker.service.exception.NumberOfGradedServiceException;

import javax.transaction.Transactional;
import java.util.List;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class NumberOfGradedService {
    NumberOfGradedRepository numberOfGradedRepository;

    @Transactional
    public List<NumberOfGraded> getAllNumbers(String email, Pageable pageable){
        try{
            return numberOfGradedRepository.findAllByProfileEmail(email, pageable);
        } catch (Exception e){
            log.error("Failed to fetch number of graded for profile with email {}, error {}, stacktrace {}",
                    email, e.getLocalizedMessage(), e.getStackTrace());
            throw new NumberOfGradedServiceException("Failed to fetch");
        }
    }
}
