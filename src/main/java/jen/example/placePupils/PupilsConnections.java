package jen.example.placePupils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PupilsConnections {
    @Getter
    private final Map<Pupil, List<Pupil>> values;
}
