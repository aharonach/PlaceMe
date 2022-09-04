package web.util;

import lombok.Getter;

import java.util.*;

public class CsvUtils {

    public static String SEPARATOR = ",";
    public static String LINE_SEPARATOR = "\n";

    public static String createLineFromValues(List<String> values){
        return new StringBuilder(String.join(SEPARATOR, values)).toString();
    }

    private static String cleanTextLine(String text)
    {
        // strips off all non-ASCII characters
        text = text.replaceAll("[^\\x00-\\x7F]", "");

        // erases all the ASCII control characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");

        // removes non-printable characters from Unicode
        text = text.replaceAll("\\p{C}", "");

        return text.trim();
    }

    public static class CsvContent{
        @Getter private final List<String> columns;
        @Getter private final List<String> rows;
        @Getter private final List<Map<String, String>> data;

        public CsvContent(String input) throws CsvNotValidException {
            String[] lines = input.split(LINE_SEPARATOR);
            this.columns = Arrays.stream(cleanTextLine(lines[0]).split(SEPARATOR)).toList();
            this.rows = Arrays.stream(lines).toList().subList(1, lines.length);
            validateRows();
            this.data = createDataList(this.columns, this.rows);
        }

        public CsvContent(List<String> columns, List<String> rows) throws CsvNotValidException {
            this.columns = new ArrayList<>(columns);
            this.rows = new ArrayList<>(rows);
            validateRows();
            this.data = createDataList(this.columns, this.rows);
        }

        public CsvContent(List<String> columns) {
            this.columns = new ArrayList<>(columns);
            this.rows = new ArrayList<>();
            this.data = new ArrayList<>();
        }

        private void validateRows() throws CsvNotValidException {
            for(String row : this.rows){
                if(row.trim().length() == 0){
                    continue;
                }

                String[] splittedRow = row.split(SEPARATOR);
                if(splittedRow.length != columns.size()){
                    throw new CsvNotValidException("line " + (rows.indexOf(row) + 1) + " is not valid.");
                }
            }
        }

        public String getHeadersLine(){
            return createLineFromValues(this.columns);
        }

        public String getFullFileContent(){
            StringBuilder stringBuilder =  new StringBuilder(getHeadersLine()).append(LINE_SEPARATOR);
            for(String row : this.rows){
                stringBuilder.append(row).append(LINE_SEPARATOR);
            }
            return stringBuilder.toString();
        }

        private List<Map<String, String>> createDataList(List<String> columns, List<String> rows) throws CsvNotValidException {
            List<Map<String, String>> data = new ArrayList<>(rows.size());

            for(String row : rows){
                if(row.trim().length() == 0){
                    continue;
                }

                Map<String, String> currentRow = new HashMap<>(columns.size());
                String[] splittedRow = cleanTextLine(row).split(SEPARATOR);
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
