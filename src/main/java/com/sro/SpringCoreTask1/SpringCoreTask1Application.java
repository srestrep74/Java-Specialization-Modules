package com.sro.SpringCoreTask1;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
import com.sro.SpringCoreTask1.util.menus.MainMenu;

public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MainMenu mainMenu = context.getBean(MainMenu.class);
        mainMenu.run();
    }
}
