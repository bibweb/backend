package ch.zuehlke.bibweb.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<BibwebUser, Long> {

    BibwebUser findByUsername(String username);

}
