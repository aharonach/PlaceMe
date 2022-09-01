package jen.web.util;

import jen.web.entity.Attribute;
import jen.web.entity.Placement;
import jen.web.entity.Pupil;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ImportExportUtils {

    Class<?> pupilClass = Pupil.class;

    public List<String> getColumnNames(Placement placement){
        List<String> columns = new ArrayList<>();

        List<Field> fields = Arrays.stream(pupilClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ImportField.class))
                .collect(Collectors.toList());

        for(Field field : fields){
            ImportField importField = field.getAnnotation(ImportField.class);
            columns.add(importField.title());
        }

        columns.add("Prefer to be with");
        columns.add("Prefer not to be with");

        for(Attribute attribute : placement.getGroup().getTemplate().getAttributes()){
            columns.add(attribute.getName().replace(",", ""));
        }

        return columns;
    }

    public Pupil createPupilFromRowMap(Map<String, String> rowMap, int lineNumber) throws ParseValueException {

        List<Constructor<?>> importConstructors = Arrays.stream(pupilClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ImportConstructor.class))
                .toList();

        if(importConstructors.isEmpty()){
            throw new CantFindImportConstructorException();
        } else if (importConstructors.size() > 1){
            throw new MoreThanOneConstructorException();
        }

        try {
            Constructor<?> constructor = importConstructors.get(0);
            List<Object> fields = getFieldList(constructor, rowMap, lineNumber);
            return  (Pupil) constructor.newInstance(fields.toArray());
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

//    public boolean isFileValid(Placement placement, String input){
//        return true;
//    }
//
//    public String createCsvFile(Placement placement) throws IOException {
//
//        try(BufferedReader csvReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("text".getBytes())))){
//            String line;
//            while((line = csvReader.readLine()) != null){
//                System.out.println(line);
//            }
//        }
//
//        return "";
//    }


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
