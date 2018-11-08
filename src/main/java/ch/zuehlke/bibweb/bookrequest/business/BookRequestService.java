package ch.zuehlke.bibweb.bookrequest.business;

import ch.zuehlke.bibweb.bookrequest.data.BookRequest;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestRepository;
import ch.zuehlke.bibweb.bookrequest.data.BookRequestState;
import ch.zuehlke.bibweb.bookrequest.exception.BookRequestNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookRequestService {
    private final BookRequestRepository bookRequestRepository;

    @Autowired
    public BookRequestService(BookRequestRepository bookRequestRepository) {
        this.bookRequestRepository = bookRequestRepository;
    }

    public List<BookRequestDTO> getAllBookRequests() {
        List<BookRequestDTO> bookRequests = new ArrayList<>();
        for (BookRequest bookRequest : this.bookRequestRepository.findAll()) {
            bookRequests.add(mapBookRequestToBookRequestDTO(bookRequest));
        }
        return bookRequests;
    }

    public List<BookRequestDTO> getBookRequestsForUser(final String username) {
        return this.bookRequestRepository.findAllByUser(username).stream()
                .map(this::mapBookRequestToBookRequestDTO).collect(Collectors.toList());
    }

    public BookRequestDTO createBookRequest(BookRequestDTO bookRequest) {
        return mapBookRequestToBookRequestDTO(this.bookRequestRepository.save(mapBookRequestDTOToBookRequest(bookRequest)));
    }

    public BookRequestDTO getBookRequestDetails(final long bookRequestId) {
        final Optional<BookRequest> bookRequest = this.bookRequestRepository.findById(bookRequestId);
        if (bookRequest.isPresent()) {
            return mapBookRequestToBookRequestDTO(bookRequest.get());
        } else {
            throw new BookRequestNotFoundException();
        }
    }

    public BookRequestDTO updateBookRequest(final BookRequestDTO bookRequest) {
        if (this.bookRequestRepository.existsById(bookRequest.getId())) {
            return mapBookRequestToBookRequestDTO(this.bookRequestRepository.save(mapBookRequestDTOToBookRequest(bookRequest)));
        } else {
            throw new BookRequestNotFoundException();
        }
    }

    public BookRequestDTO acceptBookRequest(BookRequestDTO bookRequestDto) {
        if (checkStateNotNew(bookRequestDto)) {
            throw new IllegalArgumentException("BookRequest doesn't allow to be accepted. Must be in state NEW, current " + bookRequestDto.getState());
        }
        return changeBookRequestState(bookRequestDto.getId(), BookRequestState.ACCEPTED);
    }

    public BookRequestDTO declineBookRequest(BookRequestDTO bookRequestDto) {
        if (checkStateNotNew(bookRequestDto)) {
            throw new IllegalArgumentException("BookRequest doesn't allow to be declined. Must be in state NEW, current " + bookRequestDto.getState());
        }
        return changeBookRequestState(bookRequestDto.getId(), BookRequestState.DECLINED);
    }

    private boolean checkStateNotNew(final BookRequestDTO bookRequestDto) {
        return bookRequestDto.getState() != BookRequestState.NEW;
    }

    private BookRequestDTO changeBookRequestState(final long bookRequestId, final BookRequestState newState) {
        final Optional<BookRequest> bookRequest = this.bookRequestRepository.findById(bookRequestId);
        if (bookRequest.isPresent()) {
            bookRequest.get().setState(newState);
            return mapBookRequestToBookRequestDTO(this.bookRequestRepository.save(bookRequest.get()));
        } else {
            throw new BookRequestNotFoundException();
        }
    }

    private BookRequest mapBookRequestDTOToBookRequest(BookRequestDTO dto) {
        BookRequest bookRequest = new BookRequest();
        BeanUtils.copyProperties(dto, bookRequest);
        return bookRequest;
    }

    private BookRequestDTO mapBookRequestToBookRequestDTO(BookRequest bookRequest) {
        BookRequestDTO dto = new BookRequestDTO();
        BeanUtils.copyProperties(bookRequest, dto);
        return dto;
    }
}
