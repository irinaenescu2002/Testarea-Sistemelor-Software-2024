package ro.boa.clinic.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.boa.clinic.dto.*;
import ro.boa.clinic.exception.DoctorSpecializationNotFound;
import ro.boa.clinic.exception.TicketNotFoundException;
import ro.boa.clinic.exception.UnauthorizedAccessException;
import ro.boa.clinic.model.Doctor;
import ro.boa.clinic.model.Patient;
import ro.boa.clinic.model.Status;
import ro.boa.clinic.model.Ticket;
import ro.boa.clinic.repository.TicketRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AccountService accountService;

    private boolean validateSpecialization(String specialization) {
        return doctorService.checkSpecializationExists(specialization);
    }

    @Transactional
    public Ticket createTicket(TicketCreationRequestDto ticketCreationRequest, Patient patient, Doctor assignedDoctor) {
        if (!validateSpecialization(ticketCreationRequest.specialization())) {
            throw new DoctorSpecializationNotFound();
        }

        log.info("Creating a new ticket");
        var ticket = new Ticket(assignedDoctor,
                                patient,
                                ticketCreationRequest.title(),
                                ticketCreationRequest.description(),
                                ticketCreationRequest.specialization(),
                                Status.OPENED);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket createTicket(TicketCreationRequestDto ticketCreationRequest, Patient patient) {
        var freestDoctor = doctorService.findFreestDoctorBySpecialization(ticketCreationRequest.specialization());
        return createTicket(ticketCreationRequest, patient, freestDoctor);
    }

    public TicketResponseDto getTicketDetails(Long id) {
        var ticket = ticketRepository.findById(id).orElseThrow(TicketNotFoundException::new);
        var currentRole = accountService.getAuthenticatedUserAccount().getRole();
        log.info("Returning ticket details");

        switch (currentRole) {
            case PATIENT -> {
                if (isTicketOwnedByLoggedInPatient(ticket)) {
                    return convertTicketToPatientTicketDto(ticket);
                }
                throw new TicketNotFoundException();
            }
            case DOCTOR -> {
                if (isTicketOwnedByLoggedInDoctor(ticket)) {
                    return convertTicketToDoctorTicketDto(ticket);
                }
                throw new TicketNotFoundException();
            }
            default -> throw new UnauthorizedAccessException();
        }
    }

    public Optional<Ticket> findWithDoctorByTitle(String title) {
        return ticketRepository.findWithDoctorByTitle(title);
    }

    public boolean isTicketOwnedByLoggedInPatient(Ticket ticket) {
        log.info("Checking that the id of the logged-in patient is the same as " +
                "the id of the patient associated with the ticket");
        var patientProfile = patientService.getAuthenticatedPatientProfile();
        return patientProfile.getId().equals(ticket.getPatient().getId());
    }

    public boolean isTicketOwnedByLoggedInDoctor(Ticket ticket) {
        log.info("Checking that the id of the logged-in doctor is the same as " +
                "the id of the doctor associated with the ticket");
        var doctorProfile = doctorService.getAuthenticatedDoctorProfile();
        return ticket.getDoctor() != null && doctorProfile.getId().equals(ticket.getDoctor().getId());
    }

    public TicketResponseDto updateTicketAuthenticatedUser(Long id, TicketUpdateRequestDto ticketUpdateRequest) {
        var role = accountService.getAuthenticatedUserAccount().getRole();

        var ticket = ticketRepository.findById(id);
        Ticket existingTicket = ticket.orElseThrow(TicketNotFoundException::new);

        switch (role) {
            case PATIENT -> {
                if (!isTicketOwnedByLoggedInPatient(existingTicket)) {
                    throw new TicketNotFoundException();
                }
            }
            case DOCTOR -> {
                if (!isTicketOwnedByLoggedInDoctor(existingTicket)) {
                    throw new TicketNotFoundException();
                }
            }
        }

        log.info("Updating the ticket");
        ticketUpdateRequest.status().ifPresent(status -> existingTicket.setStatus(Status.valueOf(status)));

        switch (role) {
            case PATIENT -> {
                ticketUpdateRequest.description().ifPresent(existingTicket::setDescription);
                ticketUpdateRequest.title().ifPresent(existingTicket::setTitle);

                return convertTicketToPatientTicketDto(ticketRepository.save(existingTicket));
            }
            case DOCTOR -> {
                ticketUpdateRequest.specialization().ifPresent(specialization -> {
                    if (specialization.equals(existingTicket.getSpecialization())) {
                        return;
                    }
                    if (!validateSpecialization(specialization)) {
                        throw new DoctorSpecializationNotFound();
                    }
                    existingTicket.setDoctor(doctorService.findFreestDoctorBySpecialization(specialization));
                });

                ticketUpdateRequest.specialization().ifPresent(existingTicket::setSpecialization);
                ticketUpdateRequest.response().ifPresent(existingTicket::setResponse);
                return convertTicketToDoctorTicketDto(ticketRepository.save(existingTicket));
            }
            default -> throw new UnauthorizedAccessException();
        }
    }

    public List<TicketResponseDto> getAuthenticatedUserTickets(Optional<Status> status) {
        var role = accountService.getAuthenticatedUserAccount().getRole();
        switch (role) {
            case PATIENT -> {
                var patient = patientService.getAuthenticatedPatientProfile();

                List<Ticket> tickets;
                if (status.isEmpty()) {
                    tickets = ticketRepository.getTicketsWithDoctorByPatient(patient);
                } else {
                    tickets = ticketRepository.getTicketsWithDoctorByPatientAndStatus(patient, status.get());
                }
                return tickets.stream()
                        .map(this::convertTicketToPatientTicketDto)
                        .collect(Collectors.toList());
            }
            case DOCTOR -> {
                var doctor = doctorService.getAuthenticatedDoctorProfile();

                List<Ticket> tickets;
                if (status.isEmpty()) {
                    tickets = ticketRepository.getTicketsWithPatientByDoctor(doctor);
                } else {
                    tickets = ticketRepository.getTicketsWithPatientByDoctorAndStatus(doctor, status.get());
                }
                return tickets.stream()
                        .map(this::convertTicketToDoctorTicketDto)
                        .collect(Collectors.toList());
            }
        }
        throw new UnauthorizedAccessException();
    }

    private PatientTicketResponseDto convertTicketToPatientTicketDto(Ticket ticket) {
        var doctor = ticket.getDoctor();
        String doctorName = doctor == null ? null : doctor.getFullName();
        return new PatientTicketResponseDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getSpecialization(),
                ticket.getStatus(),
                ticket.getResponse(),
                doctorName);
    }

    private DoctorTicketResponseDto convertTicketToDoctorTicketDto(Ticket ticket) {
        return new DoctorTicketResponseDto(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getSpecialization(),
                ticket.getStatus(),
                ticket.getResponse(),
                ticket.getPatient().getFullName());
    }
}