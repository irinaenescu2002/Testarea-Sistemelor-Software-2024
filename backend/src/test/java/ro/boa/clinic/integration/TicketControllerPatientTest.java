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
import ro.boa.clinic.model.Patient;
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
public class TicketControllerPatientTest {
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

    private Patient patient;

    @BeforeAll
    public void setUp() throws Exception {
        requestTester.createTestAccount(Role.PATIENT);
        patient = requestTester.createTestPatient();
        requestTester.authenticateAccount();
    }

    @Test
    void creationRequest_incorrectData_returnsError() throws Exception {
        entityTestUtils.createDoctor("Doctor", "Specialization");
        var ticketDto = new TicketCreationRequestDto("Title", "Description", "WrongSpecialization");

        mockMvc.perform(requestTester.authenticatedPost("/tickets", ticketDto))
            .andExpect(status().isBadRequest());
    }

    @Test
    void creationRequest_validData_createsTicket() throws Exception {
        entityTestUtils.createDoctor("Doctor", "Specialization");
        var ticketDto = new TicketCreationRequestDto("Title", "Description", "Specialization");

        mockMvc.perform(requestTester.authenticatedPost("/tickets", ticketDto))
            .andExpect(status().isCreated());
        var createdTicket = ticketRepository.findByTitle(ticketDto.title());

        assertEquals(ticketDto.title(), createdTicket.getTitle());
        assertEquals(ticketDto.description(), createdTicket.getDescription());
        assertEquals(ticketDto.specialization(), createdTicket.getSpecialization());
    }

    @Test
    void creationRequest_validData_assignsFreestDoctor() throws Exception {
        var doctor1 = entityTestUtils.createDoctor("Doctor1", "Specialization");
        entityTestUtils.createDoctor("Doctor2", "OtherSpecialization");
        var doctor3 = entityTestUtils.createDoctor("Doctor3", "Specialization");
        var existingTicketDto = new TicketCreationRequestDto("Title1", "Description1", "Specialization");
        ticketService.createTicket(existingTicketDto, patient, doctor1);
        var newTicketDto = new TicketCreationRequestDto("Title2", "Description", "Specialization");

        mockMvc.perform(requestTester.authenticatedPost("/tickets", newTicketDto))
            .andExpect(status().isCreated());
        var createdTicket = ticketRepository.findWithDoctorByTitle(newTicketDto.title()).orElseThrow();
        var assignedDoctor = createdTicket.getDoctor();

        assertNotNull(assignedDoctor);
        assertEquals(doctor3.getId(), assignedDoctor.getId());
    }

    @Test
    void detailsRequest_validId_returnsDetails() throws Exception {
        String ticketDetails = """
            {
              'status': 'OPENED',
              'description': 'Description',
              'specialization': 'Specialization',
              'doctorName': null,
              'response': 'Response'
            }""";

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
            .andExpect(status().isOk())
            .andExpect(content().json(ticketDetails));
    }

    @Test
    void ticketListRequest_noStatusFilter_returnsAllTickets() throws Exception {
        String ticketList = """
            [
              {
                'id': 1,
                'doctorName': null,
                'title': 'Title1',
                'description': 'Description1',
                'specialization': 'Specialization1',
                'status': 'OPENED'
              },
              {
                'id': 2,
                'doctorName': null,
                'title': 'Title2',
                'description': 'Description2',
                'specialization': 'Specialization2',
                'status': 'CLOSED'
              }
            ]""";

        ticketRepository.save(new Ticket(
            1L,
            null,
            patient,
            "Title1",
            "Description1",
            "Specialization1",
            Status.OPENED,
            null
        ));
        ticketRepository.save(new Ticket(
            2L,
            null,
            patient,
            "Title2",
            "Description2",
            "Specialization2",
            Status.CLOSED,
            null
        ));

        mockMvc.perform(requestTester.authenticatedGet("/tickets"))
            .andExpect(status().isOk())
            .andExpect(content().json(ticketList));
    }

    @Test
    void ticketListRequest_statusFilter_returnsFilteredTicketList() throws Exception {
        String openedTicketsJson = """
            [
              {
                'id': 1,
                'doctorName': null,
                'title': 'Title1',
                'description': 'Description1',
                'specialization': 'Specialization1',
                'status': 'OPENED'
              }
            ]""";
        String closedTicketsJson = """
            [
              {
                'id': 2,
                'doctorName': null,
                'title': 'Title2',
                'description': 'Description2',
                'specialization': 'Specialization2',
                'status': 'CLOSED'
              }
            ]""";
        ticketRepository.save(new Ticket(
            1L,
            null,
            patient,
            "Title1",
            "Description1",
            "Specialization1",
            Status.OPENED,
            null
        ));
        ticketRepository.save(new Ticket(
            2L,
            null,
            patient,
            "Title2",
            "Description2",
            "Specialization2",
            Status.CLOSED,
            null
        ));

        mockMvc.perform(requestTester.authenticatedGet("/tickets").param("status", Status.OPENED.toString()))
            .andExpect(status().isOk())
            .andExpect(content().json(openedTicketsJson));
        mockMvc.perform(requestTester.authenticatedGet("/tickets").param("status", Status.CLOSED.toString()))
            .andExpect(status().isOk())
            .andExpect(content().json(closedTicketsJson));
    }

    @Test
    void updateTicketRequest_validId_updatesTicket() throws Exception {
        Ticket ticket = ticketRepository.save(new Ticket(
            patient,
            "Title",
            "Description",
            "Specialization",
            Status.OPENED
        ));
        String newTicketTitle = "New title";
        String newTicketDescription = "New description";
        Status newTicketStatus = Status.CLOSED;
        TicketUpdateRequestDto updateDto = new TicketUpdateRequestDto(
            newTicketTitle,
            newTicketDescription,
            newTicketStatus,
            null,
            null
        );

        mockMvc.perform(requestTester.authenticatedPatch("/tickets/" + ticket.getId(), updateDto))
            .andExpect(status().isOk());

        assertEquals(newTicketTitle, ticket.getTitle());
        assertEquals(newTicketDescription, ticket.getDescription());
        assertEquals(newTicketStatus, ticket.getStatus());
    }
}
