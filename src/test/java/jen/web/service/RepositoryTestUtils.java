package jen.web.service;

import jen.web.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Component
@ActiveProfiles("test")
public class RepositoryTestUtils {
    @Autowired
    AttributeRepository attributeRepository;
    @Autowired
    AttributeValueRepository attributeValueRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    PlacementRepository placementRepository;
    @Autowired
    PlacementClassroomRepository placementClassroomRepository;
    @Autowired
    PlacementResultRepository placementResultRepository;
    @Autowired
    PupilRepository pupilRepository;
    @Autowired
    TemplateRepository templateRepository;

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
}
