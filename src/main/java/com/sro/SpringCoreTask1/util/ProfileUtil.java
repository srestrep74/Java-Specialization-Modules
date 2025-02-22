package com.sro.SpringCoreTask1.util;

import java.util.List;
import java.util.Random;

public class ProfileUtil {

    public static String generateUsername(String firstName, String lastName, List<String> existingUsernames) {
        String baseUsername = firstName + "." + lastName;
        
        String username = baseUsername;
        int serialNumber = 1;  

        while (existingUsernames.contains(username)) {
            username = baseUsername + serialNumber++;
        }

        return username;
    }

    public static String generatePassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        
        StringBuilder password = new StringBuilder();
        for(int i  = 0 ; i < 10 ; i++){
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
