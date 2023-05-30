package ru.springcourse.FirstSecurityApp.util;

public class PersonNotAddedException extends RuntimeException {

    public PersonNotAddedException(String message) {
        super(message);
    }
}
