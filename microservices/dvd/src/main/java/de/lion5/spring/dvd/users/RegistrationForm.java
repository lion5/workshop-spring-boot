package de.lion5.spring.dvd.users;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {

    @NotNull
    @Size(min = 8)
    @Pattern(regexp = "[a-z]*", message = "username only contains at least 8 lower case letters")
    private String username;
    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "at least 8 chars, containing digits, lower and upper case letters and special characters")
    private String password;
    @NotNull
    @NotEmpty
    private String fullName;
    private String phoneNumber;

    public User toUser(PasswordEncoder passwordEncoder, String role) {
        return new User(username, passwordEncoder.encode(password), fullName, phoneNumber, role);
    }
}
