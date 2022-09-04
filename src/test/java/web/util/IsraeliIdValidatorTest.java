package web.util;

import org.junit.jupiter.api.Test;

import static web.util.IsraeliIdValidator.ID_LENGTH;
import static org.junit.jupiter.api.Assertions.*;

class IsraeliIdValidatorTest {

    @Test
    void shouldPadWithZerosIfGivenIdContainOnlyDigitsAndLessThenIdLength() {
        String newGivenId = IsraeliIdValidator.padWithZerosAndTrim("1234");
        System.out.println(newGivenId);
        assertEquals(ID_LENGTH, newGivenId.length());
    }

    @Test
    void shouldReturnSameValueIfGivenIdContainNonDigitOrMoreThanIdLength() {
        String invalidGivenId1 = IsraeliIdValidator.padWithZerosAndTrim("12345678910");
        System.out.println(invalidGivenId1);
        assertEquals("12345678910", invalidGivenId1);

        String invalidGivenId2 = IsraeliIdValidator.padWithZerosAndTrim("text");
        System.out.println(invalidGivenId2);
        assertEquals("text", invalidGivenId2);
    }

    @Test
    void shouldReturnTrimmedValueWhenItContainsSpaces() {
        String invalidGivenId = IsraeliIdValidator.padWithZerosAndTrim(" 1234 ");
        System.out.println(invalidGivenId);
        assertEquals(ID_LENGTH, invalidGivenId.length());

        String invalidGivenId2 = IsraeliIdValidator.padWithZerosAndTrim(" 123456789 ");
        System.out.println(invalidGivenId2);
        assertEquals("123456789", invalidGivenId2);
    }
}