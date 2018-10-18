package ch.zuehlke.bibweb.bookrequest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {
    List<BookRequest> findAllByUser(String userName);
}
