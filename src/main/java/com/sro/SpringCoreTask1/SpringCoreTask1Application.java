package com.sro.SpringCoreTask1;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("myUnit");
        emf.close();
    }
}
