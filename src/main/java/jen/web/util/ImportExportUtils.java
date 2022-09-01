package jen.web.util;

import jen.web.entity.Attribute;
import jen.web.entity.Placement;
import jen.web.entity.Pupil;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class ImportExportUtils {
    public final static String PREFER_TO_BE_WITH = "Prefer to be with";
    public final static String PREFER_NOT_TO_BE_WITH = "Prefer Not to be with";

    private final static Constructor<?> pupilImportConstructor;
    private final static List<String> pupilColumns;
    private final static List<String> preferencesColumns = List.of(
            PREFER_TO_BE_WITH,
            PREFER_NOT_TO_BE_WITH
    );

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
        columns.addAll(getAttributesColumns(placement));

        return columns;
    }

    private List<String> getAttributesColumns(Placement placement){
        List<String> attributesColumns = new ArrayList<>();
        for(Attribute attribute : placement.getGroup().getTemplate().getAttributes()){
            attributesColumns.add(attribute.getName().replace(",", ""));
        }
        return attributesColumns;
    }

    public Pupil createPupilFromRowMap(Map<String, String> rowMap, int lineNumber) throws ParseValueException {

        try {
            List<Object> fields = getFieldList(pupilImportConstructor, rowMap, lineNumber);
            return  (Pupil) pupilImportConstructor.newInstance(fields.toArray());
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new DataDontFeetToConstructorException(e.getMessage());
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
    }
}