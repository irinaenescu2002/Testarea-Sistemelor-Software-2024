package ro.boa.clinic.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ro.boa.clinic.model.Account;
import ro.boa.clinic.model.Profile;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    @Modifying
    @Query("update Account set profile = :profile where email = :email and profile = null")
    int updateProfileIfUnset(@Param("email") String email, @Param("profile") Profile profile);
}
