package ru.urfu.mutual_marker.service.statistics;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.Project;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnomalyDiscoveryService {
    @Value("${anomaly.kruskalWallis.alpha:0.5}")
    Double kruskalWallisAlpha;

    @Transactional
    public boolean kruskalWallisDetectAnomaly(Project project){
        KruskalWallisTest kruskalWallisTest = new KruskalWallisTest(project.getMarks().size());
        int i = 1;
        for (Mark mark: project.getMarks()) {
            kruskalWallisTest.add(mark.getMarkValue(), i);
            i++;
        }
        return kruskalWallisTest.test(kruskalWallisAlpha);
    }
}
