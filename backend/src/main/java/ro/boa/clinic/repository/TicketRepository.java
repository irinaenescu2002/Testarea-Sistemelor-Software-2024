package ro.boa.clinic.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.boa.clinic.model.Doctor;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Status;
import ro.boa.clinic.model.Ticket;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Long> {
    Ticket findByTitle(String title);

    @EntityGraph(attributePaths = {"doctor"})
    Optional<Ticket> findWithDoctorByTitle(String title);

    @EntityGraph(attributePaths = {"patient"})
    List<Ticket> getTicketsWithPatientByDoctorAndStatus(Doctor doctor, Status status);

    @EntityGraph(attributePaths = {"patient"})
    List<Ticket> getTicketsWithPatientByDoctor(Doctor doctor);

    @EntityGraph(attributePaths = {"doctor"})
    List<Ticket> getTicketsWithDoctorByPatient(Patient patient);

    @EntityGraph(attributePaths = {"doctor"})
    List<Ticket> getTicketsWithDoctorByPatientAndStatus(Patient patient, Status status);
}
