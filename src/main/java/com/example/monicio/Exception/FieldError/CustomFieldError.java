package com.example.monicio.Exception.FieldError;

/**
 * The type Custom field error.
 *
 * @author Nikita Zhiznevskiy
 */
public class CustomFieldError {
    /**
     * The Field.
     */
    private String field;
    /**
     * The Message.
     */
    private String message;

    /**
     * Instantiates a new Custom field error.
     */
    public CustomFieldError() {
    }

    /**
     * Gets field.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets field.
     *
     * @param field the field
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
