package ch.zuehlke.bibweb.bookrequest.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRequestRepository extends CrudRepository<BookRequest, Long> {
    List<BookRequest> findAllByUser(String userName);
}
