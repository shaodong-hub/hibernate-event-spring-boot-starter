package com.github.hibernate.event.exception;

/**
 * @author 石少东
 * @date 2020-11-11 15:23
 * @since 1.0
 */


public class InvalidParameterCountException extends Exception {

    private static final long serialVersionUID = -6623551538533686039L;

    public InvalidParameterCountException() {
    }

    public InvalidParameterCountException(String message) {
        super(message);
    }
}
