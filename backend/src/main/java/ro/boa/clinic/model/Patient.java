package ro.boa.clinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients")
public class Patient extends Profile {
    @Column(name = "first_name")
    @Size(min = 2,max = 20, message = "First name cannot be shorter than 2 or longer than 20")
    @Pattern(regexp = "^[^0-9]+$", message = "First name cannot contain numbers")
    private String firstName;

    @Column(name = "last_name")
    @Size(min = 2,max = 20, message = "Last name cannot be shorter than 2 or longer than 20")
    @Pattern(regexp = "^[^0-9]+$", message = "Last name cannot contain numbers")
    private String lastName;

    @Column(name = "sex")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
}