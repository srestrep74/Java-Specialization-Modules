package com.sro.SpringCoreTask1.util;

import java.util.Random;

public class ProfileUtil {
    public static String generateUsername(String firstName, String lastName) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase();
    }

    public static String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        Random random = new Random();

        StringBuilder password = new StringBuilder();
        for(int i = 0 ; i < 10 ; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
