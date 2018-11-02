package ch.zuehlke.bibweb.config;

public final class Constants {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 18000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
