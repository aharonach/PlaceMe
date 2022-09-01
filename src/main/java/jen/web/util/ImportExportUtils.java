package jen.web.util;

import jen.web.entity.Attribute;
import jen.web.entity.Placement;
import jen.web.entity.Pupil;
import jen.web.entity.Template;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ImportExportUtils {

    Class<?> pupilClass = Pupil.class;

    public List<String> getColumnNames(Placement placement){
        List<String> columns = new ArrayList<>();

        List<Field> fields = Arrays.stream(pupilClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExportField.class))
                .collect(Collectors.toList());

        for(Field field : fields){
            ExportField exportField = field.getAnnotation(ExportField.class);
            columns.add(exportField.title());
        }

        columns.add("Prefer to be with");
        columns.add("Prefer not to be with");

        for(Attribute attribute : placement.getGroup().getTemplate().getAttributes()){
            columns.add(attribute.getName().replace(",", ""));
        }

        return columns;
    }

    public Pupil createPupilFromRowMap(Map<String, String> rowMap) {

        List<Constructor<?>> importConstructors = Arrays.stream(pupilClass.getDeclaredConstructors())
                .filter(constructor -> constructor.isAnnotationPresent(ImportConstructor.class))
                .toList();

        if(importConstructors.isEmpty()){
            throw new CantFindImportConstructorException();
        } else if (importConstructors.size() > 1){
            throw new MoreThanOneConstructorException();
        }

        Constructor<?> constructor = importConstructors.get(0);
        System.out.println(constructor);
        System.out.println(Arrays.stream(constructor.getParameterTypes()).toList());
        System.out.println(Arrays.stream(constructor.getParameterAnnotations()).toList());


//        try {
//            return  (Pupil) constructor.newInstance("dsfs", "dsfs");
//        } catch (Exception e) {
//            throw new DataDontFeetToConstructorException(e.getMessage());
//        }
        return null;
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

}
