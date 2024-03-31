package ro.boa.clinic.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ro.boa.clinic.dto.TicketCreationRequestDto;
import ro.boa.clinic.dto.TicketUpdateRequestDto;
import ro.boa.clinic.model.Doctor;
import ro.boa.clinic.model.Role;
import ro.boa.clinic.model.Status;
import ro.boa.clinic.model.Ticket;
import ro.boa.clinic.repository.TicketRepository;
import ro.boa.clinic.service.TicketService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TicketControllerDoctorTest {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestTester requestTester;

    @Autowired
    private EntityTestUtils entityTestUtils;

    private Doctor doctor;

    @BeforeAll
    public void setUp() throws Exception {
        requestTester.createTestAccount(Role.DOCTOR);
        doctor = requestTester.createTestDoctor();
        requestTester.authenticateAccount();
    }

    @Test
    void detailsRequest_validId_returnsDetails() throws Exception {
        var patient = entityTestUtils.createPatient("Patient");
        ticketRepository.save(new Ticket(
            1L,
            this.doctor,
            patient,
            "Title",
            "Description",
            "Specialization",
            Status.OPENED,
            "Response"
        ));
        String ticketDetails = """
            {
              'title': 'Title',
              'status': 'OPENED',
              'description': 'Description',
              'specialization': 'Specialization',
              'patientName': '%s',
              'response': 'Response'
            }""".formatted(patient.getFullName());

        mockMvc.perform(requestTester.authenticatedGet("/tickets/1"))
            .andExpect(status().isOk())
            .andExpect(content().json(ticketDetails));
    }

    @Test
    void detailsRequest_ticketNotOwned_returnsNotFound() throws Exception {
        var patient = entityTestUtils.createPatient("Patient");
        ticketRepository.save(new Ticket(
            1L,
            null,
            patient,
            "Title",
            "Description",
            "Specialization",
            Status.OPENED,
            "Response"
        ));

        mockMvc.perform(requestTester.authenticatedGet("/tickets/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateRequest_validDoctor_updatesTicket() throws Exception {
        var patient = entityTestUtils.createPatient("Patient");
        TicketCreationRequestDto creationDto = new TicketCreationRequestDto("Title",
                                                                            "Description",
                                                                            this.doctor.getSpecialization());
        var ticket = ticketService.createTicket(creationDto, patient, this.doctor);
        var updateTicketDto = new TicketUpdateRequestDto(Status.CLOSED,
                                                         null,
                                                         "NewResponse");

        mockMvc.perform(requestTester.authenticatedPatch("/tickets/" + ticket.getId(), updateTicketDto))
               .andExpect(status().isOk());

        assertEquals(updateTicketDto.status().get(), ticket.getStatus().name());
        assertEquals(updateTicketDto.response().get(), ticket.getResponse());
    }

    @Test
    void updateRequest_newSpecialization_assignsNewFreestDoctor() throws Exception {
        var newDoctor = entityTestUtils.createDoctor("NewDoctor", "NewSpecialization");
        var patient = entityTestUtils.createPatient("Dan");
        TicketCreationRequestDto creationDto = new TicketCreationRequestDto("Title",
                                                                            "Description",
                                                                            this.doctor.getSpecialization());
        var ticket = ticketService.createTicket(creationDto, patient, this.doctor);
        var updateTicketDto = new TicketUpdateRequestDto(null,
                                                         "NewSpecialization",
                                                         null);

        mockMvc.perform(requestTester.authenticatedPatch("/tickets/" + ticket.getId(), updateTicketDto))
               .andExpect(status().isOk());

        var assignedDoctor = ticket.getDoctor();
        assertNotNull(assignedDoctor);
        assertEquals(newDoctor.getId(), assignedDoctor.getId());
    }
}
