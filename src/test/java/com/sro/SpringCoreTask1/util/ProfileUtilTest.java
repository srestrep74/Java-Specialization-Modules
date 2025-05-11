package com.sro.SpringCoreTask1.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class ProfileUtilTest {

    @ParameterizedTest
    @CsvSource({
            "John,Doe,john.doe",
            "Maria,Rodriguez,maria.rodriguez",
            "James,O'Neill,james.o'neill"
    })
    void generateUsername_ShouldReturnExpectedUsername_WhenUsernameDoesNotExist(String firstName, String lastName,
            String expected) {
        Function<String, Boolean> alwaysFalse = username -> false;

        String result = ProfileUtil.generateUsername(firstName, lastName, alwaysFalse);

        assertEquals(expected, result);
    }

    @Test
    void generateUsername_ShouldAppendCounter_WhenUsernameExists() {
        String firstName = "John";
        String lastName = "Doe";
        Function<String, Boolean> existsFirstTimeOnly = username -> username.equals("john.doe");

        String result = ProfileUtil.generateUsername(firstName, lastName, existsFirstTimeOnly);

        assertEquals("john.doe1", result);
    }

    @Test
    void generateUsername_ShouldIncrementCounterUntilAvailable() {
        String firstName = "John";
        String lastName = "Doe";
        Set<String> existingUsernames = new HashSet<>();
        existingUsernames.add("john.doe");
        existingUsernames.add("john.doe1");
        existingUsernames.add("john.doe2");
        Function<String, Boolean> checkExisting = existingUsernames::contains;

        String result = ProfileUtil.generateUsername(firstName, lastName, checkExisting);

        assertEquals("john.doe3", result);
    }

    @Test
    void generatePassword_ShouldReturnStringWithLength10() {
        String password = ProfileUtil.generatePassword();

        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_ShouldContainOnlyValidCharacters() {
        String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        String password = ProfileUtil.generatePassword();

        for (char c : password.toCharArray()) {
            assertTrue(validChars.indexOf(c) >= 0, "Character '" + c + "' is not valid");
        }
    }

    @Test
    void generatePassword_ShouldGenerateRandomPasswords() {
        String password1 = ProfileUtil.generatePassword();
        String password2 = ProfileUtil.generatePassword();

        assertNotEquals(password1, password2, "Generated passwords should be different");
    }

    @Test
    void generatePassword_ShouldGenerateUniquePasswords() {
        int iterations = 100;
        Set<String> generatedPasswords = new HashSet<>();

        for (int i = 0; i < iterations; i++) {
            String password = ProfileUtil.generatePassword();
            generatedPasswords.add(password);
        }

        assertEquals(iterations, generatedPasswords.size(),
                "All generated passwords should be unique");
    }
}