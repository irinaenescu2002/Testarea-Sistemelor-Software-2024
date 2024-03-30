package ro.boa.clinic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ro.boa.clinic.exception.AccountDoesNotExist;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Profile;
import ro.boa.clinic.model.Role;
import ro.boa.clinic.repository.AccountRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account createDoctorAccount(String email, String password) throws DataIntegrityViolationException {
        return createAccount(email, password, Role.DOCTOR);
    }

    public Account createPatientAccount(String email, String password) throws DataIntegrityViolationException {
        return createAccount(email, password, Role.PATIENT);
    }

    /**
     * @param email    the email address of the account
     * @param password the plain text password
     * @return the created account entity
     * @throws DataIntegrityViolationException the email address is likely already used
     */
    public Account createAccount(String email, String password, Role role) throws DataIntegrityViolationException {
        log.info("Creating a new account");
        final String hashedPassword = passwordEncoder.encode(password);
        Account newAccount = new Account(email, hashedPassword, role);

        return accountRepository.save(newAccount);
    }

    public Optional<Account> findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public String getAuthenticatedUserEmail() {
        log.info("Getting authenticated user account");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    public Account getAuthenticatedUserAccount() {
        log.info("Getting authenticated user account");
        return accountRepository.findByEmail(getAuthenticatedUserEmail()).orElseThrow(AccountDoesNotExist::new);
    }

    /**
     * Links the account with the provided email to the profile, if the account has no profile associated.
     *
     * @param profile the profile to link
     * @param email   the email of the account to link
     * @return whether the profile was actually linked
     */
    public boolean linkProfileToAccount(Profile profile, String email) {
        log.info("Linking profile to account");
        var updatedRowsCount = accountRepository.updateProfileIfUnset(email, profile);
        return updatedRowsCount > 0;
    }
}
