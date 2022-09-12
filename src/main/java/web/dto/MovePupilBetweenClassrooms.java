package web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class MovePupilBetweenClassrooms {
    Long pupilId;
    Long classroomFrom;
    Long classroomTo;
}
