package web.util;


public class IsraeliIdValidator {

    public static boolean validateId = false;

    public static final int ID_LENGTH = 9;

    public static final String DIGITS_REGEX = "\\d+";

    public static String padWithZerosAndTrim(String givenId){
        String result = givenId.trim();

        if(result.length() < ID_LENGTH && result.matches(DIGITS_REGEX)){
            result = String.format("%09d", Integer.valueOf(result));
        }

        return result;
    }

    public static boolean isValid(String israeliId){
        if(validateId){
            if (israeliId.length() != ID_LENGTH)
                return false;

            int sum = 0;

            for (int i = 0; i < israeliId.length(); i++) {
                int digit = israeliId.charAt(i) - '0';
                sum += i % 2 != 0 ?
                        switch (digit) {
                            case 1 -> 2;
                            case 2 -> 4;
                            case 3 -> 6;
                            case 4 -> 8;
                            case 5 -> 1;
                            case 6 -> 3;
                            case 7 -> 5;
                            case 8 -> 7;
                            case 9 -> 9;
                            default -> 0;
                        } : digit;
            }

            return sum % 10 == 0;
        } else {
            return true;
        }
    }
}
