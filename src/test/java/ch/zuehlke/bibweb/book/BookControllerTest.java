package ch.zuehlke.bibweb.book;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Test
    public void whenValidId_thenBookShouldBeFound() throws Exception {
        Book book = new Book();
        book.setId(3000L);
        book.setTitle("Buch 1");

        given(bookService.getBookById(3000L)).willReturn(Optional.of(book));

        mvc.perform(get("/book/3000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(book.getTitle())));
    }

    @Test
    public void whenRequestingAllBooks_thenAllShouldBeReturned() throws Exception {
        Book book0 = new Book();
        book0.setId(0L);
        book0.setTitle("Buch 0");

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Buch 1");

        given(bookService.getAllBooks()).willReturn(new ArrayList<Book>() {{
            add(book0);
            add(book1);
        }});

        mvc.perform(get("/book")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].title", is(book1.getTitle())))
                .andExpect(jsonPath("$[0].title", is(book0.getTitle())));
    }
}
