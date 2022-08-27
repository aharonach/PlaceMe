package jen.web.service;

import jen.web.entity.Pupil;
import jen.web.entity.RangeAttribute;
import jen.web.entity.Template;
import jen.web.repository.*;
import jen.web.util.FieldSortingMaps;
import jen.web.util.PagesAndSortHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
@ActiveProfiles("test")
public class RepositoryTestUtils {

    @Autowired AttributeRepository attributeRepository;
    @Autowired AttributeValueRepository attributeValueRepository;
    @Autowired GroupRepository groupRepository;
    @Autowired PlacementRepository placementRepository;
    @Autowired PlacementClassroomRepository placementClassroomRepository;
    @Autowired PlacementResultRepository placementResultRepository;
    @Autowired PupilRepository pupilRepository;
    @Autowired TemplateRepository templateRepository;
    @Autowired PagesAndSortHandler pagesAndSortHandler;

    public void verifyAllTablesAreEmpty(){
        assertEquals(0, attributeRepository.findAll().size());
        assertEquals(0, attributeValueRepository.findAll().size());
        assertEquals(0, groupRepository.findAll().size());
        assertEquals(0, placementRepository.findAll().size());
        assertEquals(0, placementClassroomRepository.findAll().size());
        assertEquals(0, placementResultRepository.findAll().size());
        assertEquals(0, pupilRepository.findAll().size());
        assertEquals(0, templateRepository.findAll().size());
    }

    public void clearAllData(){
        attributeRepository.deleteAll();
        attributeValueRepository.deleteAll();
        groupRepository.deleteAll();
        placementRepository.deleteAll();
        placementClassroomRepository.deleteAll();
        placementResultRepository.deleteAll();
        pupilRepository.deleteAll();
        templateRepository.deleteAll();
    }

    public PageRequest getFirstPageRequest() throws PagesAndSortHandler.FieldNotSortableException {
        return pagesAndSortHandler.getFirstPageRequest();
    }

    public Template createTemplate1(){
        return new Template("template 1", "template 1 desc", Set.of(
                new RangeAttribute("attr 1", "attr 1 for template 1", 10),
                new RangeAttribute("attr 2", "attr 2 for template 1", 20)
        ));
    }
    public Template createTemplate2(){
        return new Template("template 2", "template 2 desc", Set.of(
                new RangeAttribute("attr 1", "attr 1 for template 2", 10),
                new RangeAttribute("attr 2", "attr 2 for template 2", 20)
        ));
    }

    public Pupil createPupil1() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        return new Pupil("123456789", "Pupil1", "Last1", Pupil.Gender.MALE, LocalDate.of(1990, 1, 1));
    }

    public Pupil createPupil2() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        return new Pupil("987654321", "Pupil2", "Last2", Pupil.Gender.FEMALE, LocalDate.of(1992, 2, 2));
    }

    public Pupil createPupil3() throws Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        return new Pupil("543216789", "Pupil3", "Last3", Pupil.Gender.MALE, LocalDate.of(1994, 4, 4));
    }
}
