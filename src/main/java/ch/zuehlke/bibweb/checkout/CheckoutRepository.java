package ch.zuehlke.bibweb.checkout;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    Optional<Checkout> findTop1ByBookIdOrderByCheckoutDateAtDesc(Long id);

}
