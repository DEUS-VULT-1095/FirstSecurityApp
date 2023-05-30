package ru.springcourse.FirstSecurityApp.util;

import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ErrorMessageCreator {

    public static String createErrorMessage(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();

        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            sb.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage());
        }
        return sb.toString();
    }
}
