package com.noveogroup.java.validator;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory compares all specialized annotation's validators in
 * one static method validate().
 * @author artem ryzhikov
 */
public final class ValidatorFactory {
    private static List<Validator> validators = new ArrayList<Validator>();
    private static Logger log = Logger.getLogger(ValidatorFactory.class.getName());

    public static void validate(final Object obj) throws ValidateException {
        List<Field> fields = new LinkedList<Field>();
        fields = Arrays.asList(obj.getClass().getDeclaredFields());
        for (int i = 0; i < fields.size(); i++) {
            final Field field = fields.get(i);
            for (Annotation annotation : field.getAnnotations()) {
                validate(obj , field , annotation);
            }
        }
    }
    private static void validate(final Object obj , final Field field , final Annotation annotation) throws
            ValidateException {
        /** flag is true when the last validated annotation was ValidatedBy and you want to go in the next child of
         * recursive tree
         */
        boolean flag = false;
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        if (annotationType.isAnnotationPresent(ValidatedBy.class)) {
            flag = true;
            final ValidatedBy validatedBy
                = annotation.annotationType().getAnnotation(ValidatedBy.class);
            final Validator validator = getInstance(validatedBy , annotation);
            try {
                validator.validate(obj , field);
            } catch (IllegalAccessException iae) {
                log.log(Level.SEVERE, iae.getMessage(), iae);
            }
        }

        //recursion for external annotations validating
        final Annotation[] annotations = annotationType.getAnnotations();
        if (flag) {
            for (Annotation annotation1 : annotations) {
                validate(obj , field , annotation1);
            }
        }

    }
    private static Validator getInstance(final ValidatedBy validatedBy , final Annotation annotation)
    {
        Validator instance = null;
        try {
            final Class validator = validatedBy.value();
            final Constructor<?> constructor = validator.getConstructor(Annotation.class);
            instance = (Validator) constructor.newInstance(annotation);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
        return instance;
    }
}
