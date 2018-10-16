package ch.zuehlke.bibweb.authentication;

import ch.zuehlke.bibweb.config.Constants;

public class AuthToken {

    private String token;
    private Long expiresIn = Constants.ACCESS_TOKEN_VALIDITY_SECONDS;

    public AuthToken() {}
    public AuthToken(String token) { this.token = token; }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
