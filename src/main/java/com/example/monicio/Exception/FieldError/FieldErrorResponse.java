package com.example.monicio.Exception.FieldError;

import java.util.List;

/**
 * The type Field error response.
 *
 * @author Nikita Zhiznevskiy
 */
public class FieldErrorResponse {
    /**
     * The Field errors.
     */
    private List<CustomFieldError> fieldErrors;

    /**
     * Instantiates a new Field error response.
     */
    public FieldErrorResponse() {
    }

    /**
     * Gets field errors.
     *
     * @return the field errors
     */
    public List<CustomFieldError> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * Sets field errors.
     *
     * @param fieldErrors the field errors
     */
    public void setFieldErrors(List<CustomFieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
