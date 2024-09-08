package com.example.demo.utils;

import java.lang.reflect.Field;

public class TestUtils {
    public static void injectObject(Object target, String fieldName, Object toInject){

        boolean wasPrivate = false;
        try{
            // this is going to give us an object of Field through reflection
            Field f = target.getClass().getDeclaredField(fieldName);

            if(!f.isAccessible()){
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if(wasPrivate){
                f.setAccessible(false);
            }

        } catch(NoSuchFieldException | IllegalAccessException e){
            e.printStackTrace();
        }
    }
}
