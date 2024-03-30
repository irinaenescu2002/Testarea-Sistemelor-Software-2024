package ro.boa.clinic.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ro.boa.clinic.model.*;
import ro.boa.clinic.service.AccountService;
import ro.boa.clinic.service.DoctorService;
import ro.boa.clinic.service.PatientService;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class RequestTester {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nullable
    private Account account;

    @Nullable
    private String jwtToken;

    public Account createTestAccount(Role role) {
        account = accountService.createAccount("user@example.com", "password", role);
        return account;
    }

    public Patient createTestPatient() {
        return patientService.createPatientProfile("John", "Doe", Sex.MALE, LocalDate.now(), account.getEmail());
    }

    public Doctor createTestDoctor() {
        return doctorService.createDoctorProfile("John", "Doe", "Specialization", account.getEmail());
    }

    public String authenticateAccount() throws Exception {
        assert account != null;
        var request = post("/login").contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(account.getUsername(), "password"));
        jwtToken = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return jwtToken;
    }

    MockHttpServletRequestBuilder addTokenToRequest(MockHttpServletRequestBuilder request) {
        assert jwtToken != null;
        return request
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
    }

    public MockHttpServletRequestBuilder authenticatedPost(String url, Object body) throws JsonProcessingException {
        var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        return addTokenToRequest(post(url).content(json));
    }

    public MockHttpServletRequestBuilder authenticatedPatch(String url, Object body) throws JsonProcessingException {
        var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        return addTokenToRequest(patch(url).content(json));
    }

    public MockHttpServletRequestBuilder authenticatedGet(String url) {
        return addTokenToRequest(get(url));
    }
}
