package web.service;

import web.entity.*;
import web.exception.NotFound;
import web.util.CsvUtils;
import web.util.OperationInfo;
import web.util.PagesAndSortHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlacementServiceTest {

    @Autowired PlacementService placementService;
    @Autowired GroupService groupService;
    @Autowired TemplateService templateService;
    @Autowired PupilService pupilService;
    @Autowired RepositoryTestUtils repositoryTestUtils;

    private final static String EXPECTED_HEADER_LINE = "Given ID,First Name,Last Name,Gender,Birth Date,Prefer to be with,Prefer Not to be with,attr 1,attr 2";
    private final static String EXPECTED_HEADER_LINE_WITH_ESCAPED_CHARS = "\uFEFFGiven ID,First Name,Last Name,Gender,Birth Date,Prefer to be with,Prefer Not to be with,attr 1,attr 2";

    @BeforeEach
    void setUp() {
        repositoryTestUtils.clearAllData();
    }

    @AfterEach
    void tearDown() {
        repositoryTestUtils.verifyAllTablesAreEmpty();
    }


    @Test
    void shouldCreateAndRemovePlacementWhenAddingPlacementWithoutGroupAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException, PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement1 = placementService.add(new Placement("placement 1", 4, null));
        Placement receivedPlacement2 = placementService.add(new Placement("placement 2", 3, null));
        assertEquals(2, getPlacementsFromService().size());
        assertNotEquals(receivedPlacement1.getId(), receivedPlacement2.getId());
        assertNotEquals(placementService.getOr404(receivedPlacement1.getId()), placementService.getOr404(receivedPlacement2.getId()));

        assertEquals("placement 1", receivedPlacement1.getName());
        assertEquals(4, receivedPlacement1.getNumberOfClasses());
        assertNull(receivedPlacement1.getGroup());
        assertNull(receivedPlacement1.getGroupId());
        placementService.deleteById(receivedPlacement1.getId());

        assertEquals("placement 2", receivedPlacement2.getName());
        assertEquals(3, receivedPlacement2.getNumberOfClasses());
        placementService.deleteById(receivedPlacement2.getId());
    }

    @Test
    void shouldCreateAndRemovePlacementWhenAddingPlacementWithGroupAndDeletingIt() throws PagesAndSortHandler.FieldNotSortableException, PlacementService.PlacementResultsInProgressException {
        Group receivedGroup1 = groupService.add(new Group("group 1", "group 1 desc", null));
        Placement receivedPlacement1 = placementService.add(new Placement("placement 1", 4, receivedGroup1));

        assertEquals("placement 1", receivedPlacement1.getName());
        assertEquals(4, receivedPlacement1.getNumberOfClasses());
        assertEquals("group 1", receivedPlacement1.getGroup().getName());
        assertEquals("group 1 desc", receivedPlacement1.getGroup().getDescription());
        assertEquals(receivedGroup1.getId(), receivedPlacement1.getGroupId());
        placementService.deleteById(receivedPlacement1.getId());

        groupService.deleteById(receivedGroup1.getId());
    }

    @Test
    void shouldThrowExceptionsOnGenerateResultWhenThereIsNoGroup() throws PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, null));
        assertThrows(PlacementService.PlacementWithoutGroupException.class, () -> placementService.generatePlacementResult(receivedPlacement, "name", "description"));
        placementService.deleteById(receivedPlacement.getId());
    }

    @Test
    void shouldThrowExceptionsOnGenerateResultWhenThereAreNoPupilsGroup() throws PlacementService.PlacementResultsInProgressException {
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", null));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));
        assertThrows(PlacementService.PlacementWithoutPupilsInGroupException.class, () -> placementService.generatePlacementResult(receivedPlacement,"name", "description"));
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldThrowExceptionWhenImportingDataWithoutGroupOrTemplate() throws PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement1 = placementService.add(new Placement("placement 1", 4));
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc"));
        Placement receivedPlacement2 = placementService.add(new Placement("placement 1", 4, receivedGroup));

        assertThrows(PlacementService.PlacementWithoutGroupException.class, () -> placementService.importDataFromCsv(receivedPlacement1, ""));
        assertThrows(PlacementService.PlacementWithoutGroupException.class, () -> placementService.exportCsvDataByPlacement(receivedPlacement1));
        assertThrows(PlacementService.PlacementWithoutGroupException.class, () -> placementService.exportCsvHeadersByPlacement(receivedPlacement1));

        assertThrows(PlacementService.PlacementWithoutTemplateInGroupException.class, () -> placementService.importDataFromCsv(receivedPlacement2, ""));
        assertThrows(PlacementService.PlacementWithoutTemplateInGroupException.class, () -> placementService.exportCsvDataByPlacement(receivedPlacement2));
        assertThrows(PlacementService.PlacementWithoutTemplateInGroupException.class, () -> placementService.exportCsvHeadersByPlacement(receivedPlacement2));

        placementService.deleteById(receivedPlacement1.getId());
        placementService.deleteById(receivedPlacement2.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldThrowExceptionWhenImportingInvalidCsvFile() throws Template.AttributeAlreadyExistException, PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutTemplateInGroupException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutGroupException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        String headerLine = placementService.exportCsvHeadersByPlacement(receivedPlacement);
        assertEquals(EXPECTED_HEADER_LINE, headerLine);

        String pupilString1 = buildPupilRow("123456789", "gal", "yeshua", "male", "28/07/1992", "", "", "5", "5");
        List<String> pupilsToImport = List.of(
                pupilString1 + CsvUtils.SEPARATOR + "dammy data"
        );
        String inputCsv = joinHeaderAndLinesToCsv(EXPECTED_HEADER_LINE_WITH_ESCAPED_CHARS, pupilsToImport);
        assertThrows(CsvUtils.CsvContent.CsvNotValidException.class, () -> placementService.importDataFromCsv(receivedPlacement, inputCsv));

        templateService.deleteById(receivedTemplate.getId());
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldReturnInfoWithErrorsWhenImportingCsvFileWithInvalidData() throws Template.AttributeAlreadyExistException, PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutTemplateInGroupException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutGroupException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        String headerLine = placementService.exportCsvHeadersByPlacement(receivedPlacement);
        assertEquals(EXPECTED_HEADER_LINE, headerLine);

        String pupilWithWrongId = buildPupilRow("sfdsfds", "gal", "yeshua", "male", "28/07/1992", "", "", "5", "5");
        String pupilWithWrongGender = buildPupilRow("123456789", "gal", "yeshua", "ML", "28/07/1992", "", "", "5", "5");
        String pupilWithWrongDate = buildPupilRow("123456789", "gal", "yeshua", "male", "28/071992", "", "", "5", "5");
        String pupilWithWrongValues = buildPupilRow("123456789", "gal", "yeshua", "male", "28/07/1992", "", "", "6", "5");
        String pupilWithWrongWantList = buildPupilRow("123456789", "gal", "yeshua", "male", "28/07/1992", "wrongId", "", "5", "5");
        String pupilWithvalidDate = buildPupilRow("123456789", "gal", "yeshua", "male", "1/7/1992", "", "", "5", "5");

        List<String> pupilsToImport = List.of(
                pupilWithWrongId,
                pupilWithWrongGender,
                pupilWithWrongDate,
                pupilWithWrongValues,
                pupilWithWrongWantList,
                pupilWithvalidDate
        );

        String inputCsv = joinHeaderAndLinesToCsv(headerLine, pupilsToImport);
        OperationInfo operationInfo = placementService.importDataFromCsv(receivedPlacement, inputCsv);
        System.out.println(operationInfo.getErrors());
        assertEquals(5, operationInfo.getErrorsCount());

        pupilService.deleteById(pupilService.getByGivenIdOr404("123456789").getId());
        templateService.deleteById(receivedTemplate.getId());
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldImportAlSuccessfullyWhenImportingCsvFileWithValidDataWithoutPreferences() throws Template.AttributeAlreadyExistException, PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutTemplateInGroupException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutGroupException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        String headerLine = placementService.exportCsvHeadersByPlacement(receivedPlacement);
        assertEquals(EXPECTED_HEADER_LINE, headerLine);

        List<String> pupilsToImport = List.of(
                buildPupilRow("111111111", "pupil1", "lastname1", "male", "1/1/1991", "", "", "1", "1"),
                buildPupilRow("222222222", "pupil2", "lastname2", "male", "2/2/1992", "", "", "2", "2"),
                buildPupilRow("333333333", "pupil2", "lastname2", "male", "3/3/1993", "", "", "3", "3")
        );

        String inputCsv = joinHeaderAndLinesToCsv(headerLine, pupilsToImport);
        OperationInfo operationInfo = placementService.importDataFromCsv(receivedPlacement, inputCsv);
        System.out.println(operationInfo.getErrors());
        assertEquals(0, operationInfo.getErrorsCount());

        Group updatedGroup = groupService.getOr404(receivedGroup.getId());
        Pupil receivedPupil1 = pupilService.getByGivenIdOr404("111111111");
        Pupil receivedPupil2 = pupilService.getByGivenIdOr404("222222222");
        Pupil receivedPupil3 = pupilService.getByGivenIdOr404("333333333");

        assertEquals(3, updatedGroup.getPupils().size());
        assertEquals("pupil1", receivedPupil1.getFirstName());
        assertEquals("lastname1", receivedPupil1.getLastName());
        assertEquals(Pupil.Gender.MALE, receivedPupil1.getGender());
        assertEquals(1991, receivedPupil1.getBirthDate().getYear());
        assertEquals(2, receivedPupil1.getAttributeValues().size());
        assertTrue(receivedPupil1.isInGroup(receivedGroup));

        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
        pupilService.deleteById(receivedPupil3.getId());
        templateService.deleteById(receivedTemplate.getId());
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldImportAllSuccessfullyWhenImportingCsvFileWithValidDataAndPreferences() throws Template.AttributeAlreadyExistException, PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutTemplateInGroupException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutGroupException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        String headerLine = placementService.exportCsvHeadersByPlacement(receivedPlacement);
        assertEquals(EXPECTED_HEADER_LINE, headerLine);

        List<String> pupilsToImport = List.of(
                buildPupilRow("111111111", "pupil1", "lastname1", "male", "1/1/1991", "222222222;333333333", "", "1", "1"),
                buildPupilRow("222222222", "pupil2", "lastname2", "male", "2/2/1992", "", "333333333", "2", "2"),
                buildPupilRow("333333333", "pupil2", "lastname2", "male", "3/3/1993", "111111111", "", "3", "3")
        );

        Pupil pupil1 = repositoryTestUtils.createPupil1();
        pupil1.setGivenId("111111111");
        pupilService.add(pupil1);

        String inputCsv = joinHeaderAndLinesToCsv(headerLine, pupilsToImport);
        OperationInfo operationInfo = placementService.importDataFromCsv(receivedPlacement, inputCsv);
        System.out.println(operationInfo.getErrors());

        Group updatedGroup = groupService.getOr404(receivedGroup.getId());
        Pupil receivedPupil1 = pupilService.getByGivenIdOr404("111111111");
        Pupil receivedPupil2 = pupilService.getByGivenIdOr404("222222222");
        Pupil receivedPupil3 = pupilService.getByGivenIdOr404("333333333");

        assertEquals(3, updatedGroup.getPupils().size());
        assertEquals("pupil1", receivedPupil1.getFirstName());
        assertEquals("lastname1", receivedPupil1.getLastName());
        assertEquals(Pupil.Gender.MALE, receivedPupil1.getGender());
        assertEquals(1991, receivedPupil1.getBirthDate().getYear());
        assertEquals(2, receivedPupil1.getAttributeValues().size());
        assertTrue(receivedPupil1.isInGroup(receivedGroup));

        assertEquals(3, groupService.getAllPreferencesForPupil(receivedGroup, receivedPupil1).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup, receivedPupil2).size());
        assertEquals(3, groupService.getAllPreferencesForPupil(receivedGroup, receivedPupil3).size());

        assertEquals(0, operationInfo.getErrorsCount());

        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
        pupilService.deleteById(receivedPupil3.getId());
        templateService.deleteById(receivedTemplate.getId());
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldImportAllSuccessfullyWhenGivenIdContainLessThanNineDigitsAndSpaces() throws Template.AttributeAlreadyExistException, PlacementService.PlacementResultsInProgressException, PlacementService.PlacementWithoutTemplateInGroupException, CsvUtils.CsvContent.CsvNotValidException, PlacementService.PlacementWithoutGroupException, Pupil.GivenIdContainsProhibitedCharsException, Pupil.GivenIdIsNotValidException {
        Template receivedTemplate = templateService.add(repositoryTestUtils.createTemplate2());
        Group receivedGroup = groupService.add(new Group("group 1", "group 1 desc", receivedTemplate));
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, receivedGroup));

        String headerLine = placementService.exportCsvHeadersByPlacement(receivedPlacement);
        assertEquals(EXPECTED_HEADER_LINE, headerLine);

        List<String> pupilsToImport = List.of(
                buildPupilRow("1111", "pupil1", "lastname1", "male", "1/1/1991", "222222222; 333333333 ", "", "1", "1"),
                buildPupilRow("222222222", "pupil2", "lastname2", "male", "2/2/1992", "", "1111", "2", "2"),
                buildPupilRow("333333333", "pupil2", "lastname2", "male", "3/3/1993", "", "", "3", "3")
        );

        String inputCsv = joinHeaderAndLinesToCsv(headerLine, pupilsToImport);
        OperationInfo operationInfo = placementService.importDataFromCsv(receivedPlacement, inputCsv);
        System.out.println(operationInfo.getErrors());
        assertEquals(0, operationInfo.getErrorsCount());

        Group updatedGroup = groupService.getOr404(receivedGroup.getId());
        Pupil receivedPupil1 = pupilService.getByGivenIdOr404("1111");
        Pupil receivedPupil2 = pupilService.getByGivenIdOr404("222222222");
        Pupil receivedPupil3 = pupilService.getByGivenIdOr404("333333333");

        assertEquals(3, groupService.getAllPreferencesForPupil(receivedGroup, receivedPupil1).size());
        assertEquals(2, groupService.getAllPreferencesForPupil(receivedGroup, receivedPupil2).size());
        assertEquals(1, groupService.getAllPreferencesForPupil(receivedGroup, receivedPupil3).size());

        assertEquals(3, updatedGroup.getPupils().size());
        assertEquals("pupil1", receivedPupil1.getFirstName());
        assertEquals("lastname1", receivedPupil1.getLastName());
        assertEquals(Pupil.Gender.MALE, receivedPupil1.getGender());
        assertEquals(1991, receivedPupil1.getBirthDate().getYear());
        assertEquals(2, receivedPupil1.getAttributeValues().size());
        assertTrue(receivedPupil1.isInGroup(receivedGroup));

        pupilService.deleteById(receivedPupil1.getId());
        pupilService.deleteById(receivedPupil2.getId());
        pupilService.deleteById(receivedPupil3.getId());
        templateService.deleteById(receivedTemplate.getId());
        placementService.deleteById(receivedPlacement.getId());
        groupService.deleteById(receivedGroup.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionOnGetPlacementWhenPlacementNotExist() {
        assertThrows(NotFound.class, () -> placementService.getOr404(100L));
    }

    @Test
    void shouldThrowResultNotExistsExceptionOnGetPlacementResultWhenPlacementDontHaveTheResult() throws PlacementService.PlacementResultsInProgressException {
        Placement receivedPlacement = placementService.add(new Placement("placement 1", 4, null));
        assertThrows(Placement.ResultNotExistsException.class, () -> placementService.getResultById(receivedPlacement, 100L));
        placementService.deleteById(receivedPlacement.getId());
    }

    private List<Placement> getPlacementsFromService() throws PagesAndSortHandler.FieldNotSortableException {
        return placementService.all(repositoryTestUtils.getFirstPageRequest()).getContent();
    }

    private String buildPupilRow(String givenId, String firstName, String lastName, String gender,
                                 String localDate, String wantToBe, String DontWantToBe, String... attrValues){
        StringBuilder stringBuilder =  new StringBuilder(givenId).append(CsvUtils.SEPARATOR)
                .append(firstName).append(CsvUtils.SEPARATOR)
                .append(lastName).append(CsvUtils.SEPARATOR)
                .append(gender).append(CsvUtils.SEPARATOR)
                .append(localDate).append(CsvUtils.SEPARATOR)
                .append(wantToBe).append(CsvUtils.SEPARATOR)
                .append(DontWantToBe).append(CsvUtils.SEPARATOR);

        for(String attrValue : attrValues){
            stringBuilder.append(attrValue).append(CsvUtils.SEPARATOR);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }

    private String joinHeaderAndLinesToCsv(String headerLine, List<String> lines){
        List<String> headerAndLines = new ArrayList<>();
        headerAndLines.add(headerLine);
        headerAndLines.addAll(lines);
        return String.join(CsvUtils.LINE_SEPARATOR, headerAndLines);
    }
}