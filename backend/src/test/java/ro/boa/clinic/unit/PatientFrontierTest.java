package ro.boa.clinic.unit;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Sex;
import ro.boa.clinic.repository.PatientRepository;
import ro.boa.clinic.service.AccountService;
import ro.boa.clinic.service.PatientService;
import org.junit.jupiter.api.Assertions;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PatientFrontierTest {
    @InjectMocks
    private PatientService patientService;

    @Mock
    private AccountService accountService;

    @Mock
    private PatientRepository patientRepository;

    //In our case there are 2 primary equivalence classes
    //These 2 are for the firstName and these are:
    // Doesn't Contain Numbers and Does Contain Numbers
    //The second class of equivalence is not relevant, since names can't contain numbers
    //But the first class is split amongst 3 more equivalence classes
    //Those are: a first name shoter than 2, a first name longer or equal to 2, but shorter or equal to 20
    //And a first name longer than 20. A test from each of those classes should be sufficient for the firstName.


    @Test
    public void ShoterThan2() {
        var firstName = "A";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);

        // Create a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Trigger validation
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
        //System.out.println(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void EqualTo2() {
        var firstName = "Al";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);

        // Create a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Trigger validation
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
        //System.out.println(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void InBetween2and20() {
        // Given
        var firstName = "John";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);

        // Create a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Trigger validation
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
        //System.out.println(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void EqualTo20() {
        // Given
        var firstName = "Keannaemilyelizebeth";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);

        // Create a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Trigger validation
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
        //System.out.println(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void LongerThan20() {
        // Given
        var firstName = "Keannaemilyelizebeths";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);

        // Create a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Trigger validation
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
        //System.out.println(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ContaintsNumbers() {
        // Given
        var firstName = "John the 2-nd";
        var lastName = "Doe";
        var sex = Sex.MALE;
        var birthdate = LocalDate.now();
        var accountEmail = "john@example.com";
        var patient = new Patient(firstName, lastName, sex, birthdate);

        // Create a Validator instance
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Trigger validation
        Set<ConstraintViolation<Patient>> violations = validator.validate(patient);
        //System.out.println(violations);
        assertFalse(violations.isEmpty());
    }
}
