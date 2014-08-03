package com.noveogroup.java.validator;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * NotBlankConstraint exists @NotBlank annotation (means String field is not empty and not null) and it's validator
 * validate()
 * @author artem ryzhikov
 */
public class NotBlankConstraint implements Validator {
    @Target(value = { METHOD,FIELD,ANNOTATION_TYPE,CONSTRUCTOR,PARAMETER } )
    @Retention(RUNTIME)
    public @interface NotBlank {
    }
    final static SizeConstraint sizeConstraint = new SizeConstraint();
    final static NotNullConstraint notNullConstraint = new NotNullConstraint();
    @Override
    public void validate(final Object obj) throws ValidateException {
        /** */
        final Field[] fields=obj.getClass().getDeclaredFields();
        for (Field f:fields) {
            if (f.isAnnotationPresent(NotBlank.class)) {
                f.setAccessible(true);
                //@Size(min=1)
                sizeConstraint.validate(f, obj, 1, sizeConstraint.INFINITE);
                //@NotNull
                notNullConstraint.validate(f , obj);
                    validate(f , obj);
            }
        }
    }
    public void validate(final Field f , final Object obj) throws ValidateException {
        if (!f.getType().equals(java.lang.String.class)) {
            throw new ValidateException("@NotBlank Not a String in" + obj.getClass().getName());
        }
    }
}
