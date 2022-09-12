package web.util;

import web.entity.*;
import web.repository.PupilRepository;
import web.service.GroupService;
import web.service.PupilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static web.util.CsvUtils.createLineFromValues;
import static web.util.IsraeliIdValidator.padWithZerosAndTrim;

@Component
@RequiredArgsConstructor
public class ImportExportUtils {
    public final static String PREFER_TO_BE_WITH = "Prefer to be with";
    public final static String PREFER_NOT_TO_BE_WITH = "Prefer Not to be with";

    private final static Constructor<?> pupilImportConstructor;
    private final static List<String> pupilColumns;
    private final static List<String> preferencesColumns = List.of(
            PREFER_TO_BE_WITH,
            PREFER_NOT_TO_BE_WITH
    );
    public static final String CLASS_NUMBER = "Class number";

    private final PupilRepository pupilRepository;
    private final PupilService pupilService;
    private final GroupService groupService;


    static {
        pupilImportConstructor = getImportConstructorOfPupil();

        List<String> pupilFieldColumns = new ArrayList<>();
        for(Parameter parameter : pupilImportConstructor.getParameters()){
            ImportField importField = parameter.getAnnotation(ImportField.class);
            pupilFieldColumns.add(importField.title());
        }
        pupilColumns = pupilFieldColumns;
    }

    private static Constructor<?> getImportConstructorOfPupil(){
        List<Constructor<?>> importConstructors = Arrays.stream(Pupil.class.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ImportConstructor.class))
                .toList();

        if(importConstructors.isEmpty()){
            throw new CantFindImportConstructorException();
        } else if (importConstructors.size() > 1){
            throw new MoreThanOneConstructorException();
        }
        return importConstructors.get(0);
    }

    public List<String> getColumnNames(Placement placement){
        List<String> columns = new ArrayList<>();

        columns.addAll(pupilColumns);
        columns.addAll(preferencesColumns);
        columns.addAll(getAttributesNames(placement));

        return columns;
    }

    public List<String> getColumnNamesWithClasses(Placement placement){
        List<String> columns = new ArrayList<>();

        columns.add(CLASS_NUMBER);
        columns.addAll(getColumnNames(placement));

        return columns;
    }

    private List<String> getAttributesNames(Placement placement){
        return getAttributesNames(getAttributesForPlacement(placement));
    }

    private List<Attribute> getAttributesForPlacement(Placement placement){
        return placement.getGroup().getTemplate().getAttributes().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    private List<String> getAttributesNames(List<Attribute> attributes){
        return attributes.stream()
                .map(Attribute::getName)
                .map(s -> s.replace(",", ""))
                .collect(Collectors.toList());
    }

    private Map<String, Attribute> getNameToAttributeMap(List<Attribute> attributes){
        Map<String, Attribute> attributeMap = new HashMap<>(attributes.size());

        for(Attribute attribute : attributes){
            attributeMap.put(attribute.getName(), attribute);
        }

        return attributeMap;
    }

    public Pupil createPupilFromRowMap(Map<String, String> rowMap, int lineNumber) throws ParseValueException {

        try {
            List<Object> fields = getFieldList(pupilImportConstructor, rowMap, lineNumber);
            return  (Pupil) pupilImportConstructor.newInstance(fields.toArray());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DataDontFeetToConstructorException(e.getMessage());
        } catch (InvocationTargetException e){
            throw new ParseValueException(e.getCause().getMessage(), lineNumber);
        }
    }

    private List<Object> getFieldList(Constructor<?> constructor, Map<String, String> rowMap, int lineNumber) throws ParseValueException {
        List<Object> fields = new ArrayList<>(constructor.getParameterCount());
        for(Parameter parameter : constructor.getParameters()){
            fields.add(getFieldValue(parameter, rowMap, lineNumber));
        }
        return fields;
    }

    private Object getFieldValue(Parameter parameter, Map<String, String> rowMap, int lineNumber) throws ParseValueException {
        ImportField importField = parameter.getDeclaredAnnotation(ImportField.class);
        String dataFromMap = rowMap.get(importField.title());
        Object value;

        try{
            if(parameter.getType().isEnum()){
                value = parameter.getType().getMethod("valueOf", String.class).invoke(parameter, dataFromMap.toUpperCase());
            } else if(parameter.getType().equals(LocalDate.class)){
                value = LocalDate.parse(dataFromMap, DateTimeFormatter.ofPattern("d/M/u"));
            } else {
                try {
                    value = parameter.getType().getDeclaredConstructor(parameter.getType()).newInstance(dataFromMap);
                } catch (Exception e) {
                    throw new TypeNotSupportedException(parameter.getType().getTypeName());
                }
            }
        } catch (Exception e) {
            throw new ParseValueException(importField.title(), dataFromMap, lineNumber);
        }

        return value;
    }

    public List<String> createRowDataForPupils(Group group, List<String> columns, Collection<Pupil> pupils, Map<String, String> additionalInfo) throws Group.PupilNotBelongException, IllegalAccessException, NoSuchFieldException {
        List<String> rows = new ArrayList<>(group.getNumberOfPupils());

        List<ImportField> importFields = Arrays.stream(pupilImportConstructor.getParameters())
                .map(parameter -> parameter.getAnnotation(ImportField.class))
                .toList();

        for(Pupil pupil : pupils){
            Map<String, String> pupilDataMap = new HashMap<>(columns.size());
            pupilDataMap.putAll(additionalInfo);

            // add pupil data
            for(ImportField importField : importFields){
                Field field = pupil.getClass().getDeclaredField(importField.fieldName());
                field.setAccessible(true);
                pupilDataMap.put(importField.title(), String.valueOf(field.get(pupil)));
            }

            // add preferences values data
            pupilDataMap.put(PREFER_TO_BE_WITH, getGivenIds(group, pupil, true));
            pupilDataMap.put(PREFER_NOT_TO_BE_WITH, getGivenIds(group, pupil, false));

            // add attribute values data
            for(AttributeValue attributeValue : pupil.getAttributeValues(group)){
                pupilDataMap.put(attributeValue.getAttribute().getName(), String.valueOf(attributeValue.getValue()));
            }

            List<String> values = new ArrayList<>();
            for(String column : columns){
                values.add(pupilDataMap.get(column));
            }
            rows.add(createLineFromValues(values));
        }

        return rows;
    }

    public List<String> createRowDataForPupils(Group group, List<String> columns) throws Group.PupilNotBelongException, IllegalAccessException, NoSuchFieldException {
        return createRowDataForPupils(group, columns, group.getPupils(), new HashMap<>());
    }

    private String getGivenIds(Group group, Pupil pupil, boolean wantsToBeTogether){
        Set<Long> wantToBeWithIds = group.getAllPreferencesForPupil(pupil.getId()).stream()
                .filter(preference ->  preference.getIsSelectorWantToBeWithSelected() == wantsToBeTogether)
                .map(preference -> preference.getSelectorSelectedId().getSelectedId())
                .collect(Collectors.toSet());
        return pupilRepository.getAllByIdIn(wantToBeWithIds).stream()
                .map(Pupil::getGivenId)
                .collect(Collectors.joining(";"));
    }

    public OperationInfo parseAndAddDataFromFile(CsvUtils.CsvContent csvContent, Placement placement) throws CsvUtils.CsvContent.CsvNotValidException {
        if(!csvContent.getHeadersLine().equals(createLineFromValues(getColumnNames(placement)))){
            throw new CsvUtils.CsvContent.CsvNotValidException("Attributes not match for this placement.");
        }

        OperationInfo operationInfo = new OperationInfo();
        List<Map<String, String>> contentData = csvContent.getData();
        Group group = placement.getGroup();
        Map<String, Attribute> attributeMap = getNameToAttributeMap(getAttributesForPlacement(placement));

        Map<String, Pupil> givenIdToPupilMap = getGivenIdToPupilMap(operationInfo, contentData, group);
        int lineNumber = 2; // first line + headers

        for(Map<String, String> rowMap : contentData){
            List<String> errors;
            String currentGivenId;

            try {
                currentGivenId = createPupilFromRowMap(rowMap, lineNumber).getGivenId();
            } catch (ImportExportUtils.ParseValueException ignored) {
                // parse error already added to operationInfo while building givenIdToPupilMap
                continue;
            }

            Pupil currentPupil = givenIdToPupilMap.get(currentGivenId);
            if(currentGivenId != null && currentPupil != null){
                // add preferences
                errors = addPreferencesFromImportData(rowMap.get(PREFER_TO_BE_WITH), givenIdToPupilMap, group, currentPupil, true, lineNumber);
                operationInfo.addErrors(errors);

                errors = addPreferencesFromImportData(rowMap.get(PREFER_NOT_TO_BE_WITH), givenIdToPupilMap, group, currentPupil, false, lineNumber);
                operationInfo.addErrors(errors);

                // add attribute values
                errors = addAttributeValuesForPupil(currentPupil, group, attributeMap, lineNumber, rowMap);
                operationInfo.addErrors(errors);
            }

            lineNumber++; // number for parse messages error message
        }

        return operationInfo;
    }

    private Map<String, Pupil> getGivenIdToPupilMap(OperationInfo operationInfo, List<Map<String, String>> contentData, Group group){
        Map<String, Pupil> givenIdToPupilMap = new HashMap<>(contentData.size());
        int lineNumber = 2; // first line + headers

        for(Map<String, String> rowMap : contentData){
            String currentGivenId;

            try {
                Pupil newPupil = createPupilFromRowMap(rowMap, lineNumber);
                Pupil receivedPupil = pupilService.updateOrCreatePupilByGivenId(newPupil);
                receivedPupil.addToGroup(group);
                currentGivenId = receivedPupil.getGivenId();
                givenIdToPupilMap.put(currentGivenId, receivedPupil);
            } catch (ImportExportUtils.ParseValueException | Pupil.GivenIdContainsProhibitedCharsException | Pupil.GivenIdIsNotValidException e) {
                operationInfo.addError(e.getMessage());
            }
            lineNumber ++; // number for parse messages error message
        }

        return givenIdToPupilMap;
    }

    private List<String> addAttributeValuesForPupil(Pupil pupil, Group group, Map<String, Attribute> attributeMap,
                                                    int lineNumber, Map<String, String> rowMap){
        List<String> errors = new ArrayList<>();

        Map<Long, Double> attributeValues = new HashMap<>(attributeMap.size());
        for(String name : attributeMap.keySet()){
            double value = rowMap.get(name).isEmpty() ? 0 : Double.parseDouble(rowMap.get(name));
            attributeValues.put(attributeMap.get(name).getId(), value);
        }

        try {
            pupilService.addOrUpdateAttributeValuesFromIdValueMap(pupil, group, attributeValues);
        } catch (Group.PupilNotBelongException | Template.AttributeNotBelongException |
                 AttributeValue.ValueOutOfRangeException e) {
            errors.add("Line " + lineNumber + ". Attribute values error: " + e.getMessage());
        }

        return errors;
    }

    // get string list of given ids from import file in format 123456789;546845678 and add them as preferences
    private List<String> addPreferencesFromImportData(String listString, Map<String, Pupil> givenIdToPupilMap, Group group,
                                                      Pupil selector, boolean WantToBeTogether, int lineNumber){
        List<String> errors = new ArrayList<>();
        if(listString != null && !listString.isBlank()){
            for(String selectedGivenId : listString.split(";")){
                try{
                    String paddedGivenId = padWithZerosAndTrim(selectedGivenId);
                    Pupil.validateGivenId(paddedGivenId);
                    Pupil selected = givenIdToPupilMap.get(paddedGivenId);
                    if( selected == null){
                        throw new CantFindPupilException(paddedGivenId);
                    }
                    groupService.addPupilPreference(group, new Preference(selector, selected, WantToBeTogether));
                } catch (Preference.SamePupilException | Pupil.GivenIdContainsProhibitedCharsException |
                         Pupil.GivenIdIsNotValidException | CantFindPupilException |
                         Group.PupilNotBelongException e) {
                    errors.add("Line " + lineNumber + ". Preferences error: " + e.getMessage());
                }
            }
        }
        return errors;
    }

    public List<String> getRowsForPupilsWithClasses(Group group, List<String> columns, Collection<PlacementClassroom> placementClassrooms) throws Group.PupilNotBelongException, NoSuchFieldException, IllegalAccessException {
        List<String> rows = new ArrayList<>(group.getNumberOfPupils());
        int classNumber = 1;
        for(PlacementClassroom placementClassroom : placementClassrooms){
            Map<String, String> additionalInfo = new HashMap<>();
            additionalInfo.put(CLASS_NUMBER, String.valueOf(classNumber));
            List<String> classRows = createRowDataForPupils(group, columns, placementClassroom.getPupils(), additionalInfo);
            rows.addAll(classRows);
            classNumber++;
        }
        return rows;
    }

    public static class CantFindImportConstructorException extends RuntimeException {
        public CantFindImportConstructorException(){
            super("Cant create pupil from data. no import constructor.");
        }
    }

    public static class MoreThanOneConstructorException extends RuntimeException {
        public MoreThanOneConstructorException(){
            super("Cant create pupil from data. more than one constructor available.");
        }
    }

    public static class DataDontFeetToConstructorException extends RuntimeException {
        public DataDontFeetToConstructorException(String message){
            super("Cant create pupil from data. " + message);
        }
    }

    public static class TypeNotSupportedException extends RuntimeException {
        public TypeNotSupportedException(String type){
            super("Type '" + type + "' is not supported.");
        }
    }

    public static class ParseValueException extends Exception {
        public ParseValueException(String column, String data, int lineNumber){
            super("Line " + lineNumber + ". Cant parse value '" + data + "' for column '" + column + "'.");
        }

        public ParseValueException(String message, int lineNumber){
            super("Line " + lineNumber + ". " + message);
        }
    }

    public static class CantFindPupilException extends Exception {
        public CantFindPupilException(String givenId){
            super("Cant find pupil with given id: " + givenId + "'.");
        }
    }
}