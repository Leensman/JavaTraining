package com.noveogroup.java.validator;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * -//- (see NotBlankConstraint)
 * @author artem ryzhikov
 */
public class PatternValidator implements Validator {
    private final static Logger LOG = Logger.getLogger(PatternValidator.class.getName());
    private final Pattern annotation;
    private boolean checkWithRegExp(final String regexp , final String userNameString) {
        final java.util.regex.Pattern p = java.util.regex.Pattern.compile(regexp);
        final Matcher m = p.matcher(userNameString);
        return m.matches();
    }
    public PatternValidator(final Annotation annotation) {
        this.annotation = (Pattern) annotation;
    }
    @Override
    public void validate(final Object obj , final Field field) throws ValidateException {

            field.setAccessible(true);
            String value = new String();
            try {
                value = field.get(obj).toString();
            } catch (IllegalAccessException e) {
                LOG.log(Level.FINE, "IllegalAccessException in Object" +
                        obj.getClass().getName() +
                        "\n in field" +
                        field.getName());
            }
            String message = "@Pattern constraint ";
            try {
                if (!checkWithRegExp(field.getAnnotation(Pattern.class).regexp(),
                        (String) field.get(obj))) {
                    throw new ValidateException(message , field.getName() , field.get(obj));
                }
            } catch (PatternSyntaxException e) {
                try {
                    throw new ValidateException(message, field.getName(), field.get(obj));
                } catch (IllegalAccessException iae){

               }

            } catch (IllegalAccessException e) {
                LOG.log(Level.SEVERE, "IllegalAccessException in RegExp:", e);
            }


    }
}

