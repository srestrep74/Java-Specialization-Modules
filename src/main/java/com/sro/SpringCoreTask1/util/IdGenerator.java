package com.sro.SpringCoreTask1.util;

public class IdGenerator {

    private static Long id = 6L;

    public static Long generateId() {
        return id++;
    }
}
