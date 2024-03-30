package ro.boa.clinic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Patient;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p JOIN Account a ON p.id = a.profile.id WHERE a.email = :email")
    Patient findPatientProfileByEmail(@Param("email") String email);
}
