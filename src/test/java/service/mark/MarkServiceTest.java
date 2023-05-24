package service.mark;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;
import ru.urfu.mutual_marker.service.mark.MarkService;

@ContextConfiguration(classes = {
        MarkService.class
})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class MarkServiceTest {
}
