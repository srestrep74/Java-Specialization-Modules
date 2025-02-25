package com.sro.SpringCoreTask1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.sro.SpringCoreTask1.config.AppConfig;
public class SpringCoreTask1Application {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        ConsoleMenu consoleMenu = context.getBean(ConsoleMenu.class);
        consoleMenu.run();
    }
}
