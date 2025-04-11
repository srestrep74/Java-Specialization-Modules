package com.sro.SpringCoreTask1.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
    boolean requireTrainee() default false;
    boolean requireTrainer() default false;
}
