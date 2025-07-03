package com.swpteam.smokingcessation.utils;

import com.swpteam.smokingcessation.constant.ErrorCode;
import com.swpteam.smokingcessation.domain.entity.Account;
import com.swpteam.smokingcessation.exception.AppException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ValidationUtil {

    public void checkFieldExist(Class<?> clazz, String fieldName) {
        if (clazz == null || fieldName == null) {
            return;
        }

        if (!isFieldExist(clazz, fieldName))
            throw new AppException(ErrorCode.INVALID_FIELD);
    }

    private boolean isFieldExist(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while(current != null){
            try{
                current.getDeclaredField(fieldName);
                return true;
            } catch (NoSuchFieldException e) {
                //move to superclasses
                current = current.getSuperclass();
            }
        }
        return false;
    }
}
