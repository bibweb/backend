package ch.zuehlke.bibweb.book;
import ch.zuehlke.bibweb.book.exception.*;
import ch.zuehlke.bibweb.config.WebSecurityTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
@ContextConfiguration(classes = {WebSecurityTestConfig.class, BookController.class})
@WebAppConfiguration
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
    public void whenRequestingAllBooksAndNotAuthenticated_thenShouldBeForbidden() throws Exception {
        mvc.perform(get("/books")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenRequestingABookAndNotAuthenticated_thenShouldBeForbidden() throws Exception {
        mvc.perform(get("/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenUpdatingBookAndNotAdmin_thenShouldBeForbidden() throws Exception {
        updateRequestShouldBeForbidden();
    }

    private void updateRequestShouldBeForbidden() throws Exception {
        final BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("updatedTitle");

        doNothing().when(bookService).updateBook(any(Long.class), any(BookDTO.class));

        this.mvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(book)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdatingBookAndNotAuthenticated_thenShouldBeForbidden() throws Exception {
        updateRequestShouldBeForbidden();
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

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenReservingBook_thenStatusShouldIsCreated() throws Exception {
        this.mvc.perform(put("/books/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenReservingBookAndNotPossible_thenStatusShouldBeForbidden() throws Exception {
        Mockito.when(bookService.reserveBook(1L)).thenThrow(BookCannotBeReservedException.class);

        this.mvc.perform(put("/books/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenReservingBookAndAlreadyExistsForUser_thenStatusShouldBeNoContent() throws Exception {
        Mockito.when(bookService.reserveBook(1L)).thenThrow(ReservationAlreadyExistsForUserException.class);

        this.mvc.perform(put("/books/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenDeletingReservationForOtherUser_thenStatusShouldBeForbidden() throws Exception {
        Mockito.doThrow(CannotDeleteReservationForOtherUserException.class).when(bookService).deleteActiveReservationForBook(1L);

        this.mvc.perform(delete("/books/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenDeletingNonExistingReservation_thenStatusShouldBeNotFound() throws Exception {
        Mockito.doThrow(ReservationDoesNotExistException.class).when(bookService).deleteActiveReservationForBook(1L);

        this.mvc.perform(delete("/books/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    public void whenDeletingExistingReservation_thenStatusShouldBeNoContent() throws Exception {
        this.mvc.perform(delete("/books/1/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
