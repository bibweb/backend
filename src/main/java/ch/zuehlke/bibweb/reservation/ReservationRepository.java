package ch.zuehlke.bibweb.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findTop1ByBookIdOrderByReservedAtDesc(Long id);

}
