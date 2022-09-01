package jen.web.util;

import lombok.Getter;

import java.util.*;

public class CsvUtils {

    public static String SEPARATOR = ",";
    public static String LINE_SEPARATOR = "\n";

    public static class CsvContent{
        @Getter private final List<String> columns;
        @Getter private final List<String> rows;
        @Getter private final List<Map<String, String>> data;

        public CsvContent(String input) throws CsvNotValidException {
            String[] lines = input.split(LINE_SEPARATOR);
            this.columns = Arrays.stream(lines[0].split(SEPARATOR)).toList();
            this.rows = Arrays.stream(lines).toList().subList(1, lines.length);
            this.data = createDataList(columns, rows);
        }

        public CsvContent(List<String> columns) throws CsvNotValidException {
            this.columns = new ArrayList<>(columns);
            this.rows = new ArrayList<>();
            this.data = new ArrayList<>();
        }

        public String getHeadersLine(){
            return new StringBuilder(String.join(SEPARATOR, this.columns)).toString();
        }

        private List<Map<String, String>> createDataList(List<String> columns, List<String> rows) throws CsvNotValidException {
            List<Map<String, String>> data = new ArrayList<>(rows.size());

            for(String row : rows){
                if(row.trim().length() == 0){
                    continue;
                }

                Map<String, String> currentRow = new HashMap<>(columns.size());
                String[] splittedRow = row.split(SEPARATOR);
                if(splittedRow.length != columns.size()){
                    throw new CsvNotValidException("line " + rows.indexOf(row) + 1 + " is not valid.");
                }

                for(String column : columns){
                    currentRow.put(column, splittedRow[columns.indexOf(column)]);
                }
                data.add(currentRow);
            }

            return data;
        }

        public static class CsvNotValidException extends Exception{
            CsvNotValidException(String message){
                super(message);
            }
        }
    }

}
