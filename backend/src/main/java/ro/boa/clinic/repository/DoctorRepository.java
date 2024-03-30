package ro.boa.clinic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.boa.clinic.model.Doctor;

import java.util.List;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Long> {
    @Query("select specialization from Doctor group by specialization")
    List<String> listAllSpecializations();

    boolean existsDoctorBySpecialization(@Param("specialization") String specialization);

    @Query("SELECT d FROM Doctor d JOIN Account a ON d.id = a.profile.id WHERE a.email = :email")
    Doctor findDoctorProfileByEmail(@Param("email") String email);

    @Query("FROM Doctor d WHERE d.specialization = :specialization ORDER BY " +
            "(SELECT COUNT(t.id) FROM Ticket t WHERE t.doctor IS NOT NULL AND t.status = 'OPENED' AND  t.doctor = d) " +
            "ASC LIMIT 1")
    Doctor findFreestDoctorBySpecialization(@Param("specialization") String specialization);
}
