package ch.zuehlke.bibweb.book;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    /*@Test
    public void whenGetByValidId_thenReturnBook() {
        Book book = new Book();
        book.setId(5L);
        book.setTitle("EM Book 0");

        entityManager.merge(book);
        entityManager.flush();

        Optional<Book> found = bookRepository.findById(book.getId());

        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(book.getTitle(), found.get().getTitle());
    }*/

}
