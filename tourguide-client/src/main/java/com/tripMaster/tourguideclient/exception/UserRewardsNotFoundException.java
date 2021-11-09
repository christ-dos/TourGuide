package com.tripMaster.tourguideclient.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Class that handles exceptions when user rewards not exist
 *
 * @author Christine Duarte
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserRewardsNotFoundException extends RuntimeException {

    public UserRewardsNotFoundException(String message) {
        super(message);
    }
}

