package com.mewebstudio.javaspringbootboilerplate.util;

import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

public final class Constants {
    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    public static final String TOKEN_HEADER = "Authorization";

    public static final String TOKEN_TYPE = "Bearer";

    public static final int EMAIL_VERIFICATION_TOKEN_LENGTH = 64;

    public static final int PASSWORD_RESET_TOKEN_LENGTH = 32;

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final int PASSWORD_MIN_LENGTH = 8; 

    public static final int PASSWORD_MAX_LENGTH = 100;

    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    public static final String PASSWORD_REGEX = "^.+$";
    public static final String PHONE_REGEX = "^\\+?[1-9][0-9]{7,14}$";

    private Constants() {
    }

    @Getter
    @AllArgsConstructor
    public enum RoleEnum {
        ADMIN("ADMIN"),
        USER("USER");

        private final String value;

        public static RoleEnum get(final String name) {
            return Stream.of(RoleEnum.values())
                .filter(p -> p.name().equals(name.toUpperCase()) || p.getValue().equals(name.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid role name: %s", name)));
        }
    }

    public static String getTokenFromPath(final String path) {
        if (path == null || path.isEmpty())
            return null;

        final String[] fields = path.split("/");

        if (fields.length == 0)
            return null;

        try {
            return fields[2];
        } catch (final IndexOutOfBoundsException e) {
            System.out.println("Cannot find user or channel id from the path!. Ex:"+ e.getMessage());
        }
        return null;
    }
}
