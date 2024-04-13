package ro.boa.clinic.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ro.boa.clinic.dto.TicketCreationRequestDto;
import ro.boa.clinic.exception.DoctorSpecializationNotFound;
import ro.boa.clinic.model.*;
import ro.boa.clinic.repository.TicketRepository;
import ro.boa.clinic.service.DoctorService;
import ro.boa.clinic.service.PatientService;
import ro.boa.clinic.service.TicketService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private DoctorService doctorService;

    @Mock
    private PatientService patientService;

    @Test
    public void createTicketRequest_specializationFound_createTicket() {
        // Given
        var firstNamePatient = "John";
        var lastNamePatient = "Doe";
        var sexPatient = Sex.MALE;
        var birthdatePatient = LocalDate.now();
        var patient = new Patient(firstNamePatient, lastNamePatient, sexPatient, birthdatePatient);

        var firstNameDoctor = "Jane";
        var lastNameDoctor = "Doe";
        var specializationDoctor = "cardiology";
        var doctor = new Doctor(firstNameDoctor, lastNameDoctor, specializationDoctor);

        var ticket = new TicketCreationRequestDto("A", "B", "cardiology");
        var returnedticket = new Ticket(doctor,
                patient,
                ticket.title(),
                ticket.description(),
                ticket.specialization(),
                Status.OPENED);
        when(doctorService.checkSpecializationExists(any())).thenReturn(true);
        when(ticketRepository.save(any())).thenReturn(returnedticket);

        // When
        var ticketCreated = ticketService.createTicket(ticket, patient, doctor);

        // Then
        assertEquals(ticket.description(), ticketCreated.getDescription());
        assertEquals(ticket.title(), ticketCreated.getTitle());
        assertEquals(ticket.specialization(), ticketCreated.getSpecialization());
    }

    @Test
    public void createTicketRequest_specializationNotFound_throwDoctorSpecializationNotFound() {
        // Given
        var firstNamePatient = "John";
        var lastNamePatient = "Doe";
        var sexPatient = Sex.MALE;
        var birthdatePatient = LocalDate.now();
        var patient = new Patient(firstNamePatient, lastNamePatient, sexPatient, birthdatePatient);

        var firstNameDoctor = "Jane";
        var lastNameDoctor = "Doe";
        var specializationDoctor = "cardiology";
        var doctor = new Doctor(firstNameDoctor, lastNameDoctor, specializationDoctor);

        var ticket = new TicketCreationRequestDto("A", "B", "cardiology");

        when(doctorService.checkSpecializationExists(any())).thenReturn(false);

        // When
        // Then
        assertThrows(DoctorSpecializationNotFound.class,
                () -> ticketService.createTicket(ticket, patient, doctor));
    }

    @Test
    public void isTicketOwnedByLoggedInPatient_ticketNotOwned_returnFalse() {
        // Given
        var patient = new Patient("John", "Doe", Sex.MALE, LocalDate.now());
        patient.setId(1L);
        var ticket = new Ticket(patient, "", "", "", Status.OPENED);

        var otherPatient = new Patient("Jennie", "Doe", Sex.FEMALE, LocalDate.now());
        otherPatient.setId(2L);

        when(patientService.getAuthenticatedPatientProfile()).thenReturn(otherPatient);

        // When
        // Then
        assertFalse(ticketService.isTicketOwnedByLoggedInPatient(ticket));
    }
}
