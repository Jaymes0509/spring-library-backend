package tw.ispan.librarysystem.dto.member;

public class MemberLoginDto {
    private String email;
    private String password;

    // Getter & Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}