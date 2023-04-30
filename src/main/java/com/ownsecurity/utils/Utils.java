package com.ownsecurity.utils;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Component
public class Utils<T> {

    public T changeFields(T objToChange, T objWithUpdatedFields) {
        try {
            Field[] fullFields = objToChange.getClass().getDeclaredFields();

            Arrays.stream(fullFields).forEach(fullField -> {
                fullField.setAccessible(true);
                Object value = null;
                try {
                    value = fullField.get(objWithUpdatedFields);
                    if (value != null) {
                        fullField.set(objToChange, value);
                    }
                } catch (IllegalAccessException e) {
                    System.err.println(e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return objToChange;
    }

}
