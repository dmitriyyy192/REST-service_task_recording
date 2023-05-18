package com.ownsecurity.exception;

import java.util.function.Supplier;

public class TodoNotFoundException extends Exception implements Supplier {
    public TodoNotFoundException(String message) {
        super(message);
    }

    @Override
    public Object get() {
        return null;
    }
}
