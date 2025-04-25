package com.sro.SpringCoreTask1.util;

import java.util.Random;
import java.util.function.Function;

public class ProfileUtil {
    public static String generateUsername(String firstName, String lastName, Function<String, Boolean> existsByUsername) {
        String baseUsername = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String username = baseUsername;
        int counter = 1;
        while (existsByUsername.apply(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    public static String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
