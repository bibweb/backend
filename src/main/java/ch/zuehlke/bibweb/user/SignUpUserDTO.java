package ch.zuehlke.bibweb.user;

public class SignUpUserDTO {
    private String username;
    private String password;

    public SignUpUserDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
