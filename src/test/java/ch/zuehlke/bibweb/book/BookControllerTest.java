package ch.zuehlke.bibweb.book;
import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.checkout.CheckoutService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
@ActiveProfiles("unit-test")
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Test
    @WithMockUser(roles = "USER")
    public void whenValidId_thenBookShouldBeFound() throws Exception {
        BookDTO book = new BookDTO();
        book.setId(3000L);
        book.setTitle("Buch 1");

        given(bookService.getBookById(3000L)).willReturn(book);

        mvc.perform(get("/books/3000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenNonExistingId_thenStatusIsNotFound() throws Exception {
        given(bookService.getBookById(3000L)).willThrow(BookNotFoundException.class);

        mvc.perform(get("/books/3000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void whenRequestingAllBooks_thenAllShouldBeReturned() throws Exception {
        BookDTO book0 = new BookDTO();
        book0.setId(0L);
        book0.setTitle("Buch 0");

        BookDTO book1 = new BookDTO();
        book1.setId(1L);
        book1.setTitle("Buch 1");

        given(bookService.getAllBooks()).willReturn(new ArrayList<BookDTO>() {{
            add(book0);
            add(book1);
        }});

        mvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].title", is(book1.getTitle())))
                .andExpect(jsonPath("$[0].title", is(book0.getTitle())));
    }

    @Test
    public void whenRequestingAllBooksAndNotAuthenticated_thenShouldBeUnauthorized() throws Exception {
        mvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenRequestingABookAndNotAuthenticated_thenShouldBeUnauthorized() throws Exception {
        mvc.perform(get("/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenUpdatingBookAndNotAdmin_thenShouldBeForbidden() throws Exception {
        final BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("updatedTitle");

        doNothing().when(bookService).updateBook(any(Long.class), any(BookDTO.class));

        this.mvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isForbidden());
    }

    private void updateRequestShouldBeUnauthorized() throws Exception {
        final BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("updatedTitle");

        doNothing().when(bookService).updateBook(any(Long.class), any(BookDTO.class));

        this.mvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdatingBookAndNotAuthenticated_thenShouldBeUnauthorized() throws Exception {
        updateRequestShouldBeUnauthorized();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void whenUpdatingBookAndAdmin_thenShouldBeOk() throws Exception {
        final BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("updatedTitle");

        doNothing().when(bookService).updateBook(any(Long.class), any(BookDTO.class));

        this.mvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isOk());
    }
}
