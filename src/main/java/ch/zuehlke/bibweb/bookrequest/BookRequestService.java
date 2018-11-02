package ch.zuehlke.bibweb.bookrequest;

import ch.zuehlke.bibweb.bookrequest.exception.BookRequestNotFoundException;
import ch.zuehlke.bibweb.user.UserSecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookRequestService {

    @Autowired
    private BookRequestRepository bookRequestRepository;

    public List<BookRequestDTO> getBookRequests() {
        if (UserSecurityUtil.currentUserHasRole("ROLE_ADMIN")) {
            return this.bookRequestRepository.findAll().stream().map(this::mapBookRequestToBookRequestDTO).collect(Collectors.toList());
        } else {
            return this.bookRequestRepository.findAllByUser(UserSecurityUtil.currentUserName())
                    .stream()
                    .map(this::mapBookRequestToBookRequestDTO)
                    .collect(Collectors.toList());
        }
    }

    public BookRequestDTO createBookRequest(BookRequestDTO bookRequest) {
        bookRequest.setUser(UserSecurityUtil.currentUserName());
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

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public BookRequestDTO updateBookRequest(final BookRequestDTO bookRequest) {
        if (this.bookRequestRepository.existsById(bookRequest.getId())) {
            return mapBookRequestToBookRequestDTO(this.bookRequestRepository.save(mapBookRequestDTOToBookRequest(bookRequest)));
        } else {
            throw new BookRequestNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public BookRequestDTO acceptBookRequest(BookRequestDTO bookRequestDto) {
        if (bookRequestDto.getState() != BookRequestState.NEW) {
            throw new IllegalArgumentException("BookRequest doesn't allow to be accepted. Must be in state NEW, current " +bookRequestDto.getState());
        }

        final Optional<BookRequest> bookRequest = this.bookRequestRepository.findById(bookRequestDto.getId());
        if (bookRequest.isPresent()) {
            bookRequest.get().setState(BookRequestState.ACCEPTED);
            return mapBookRequestToBookRequestDTO(this.bookRequestRepository.saveAndFlush(bookRequest.get()));

        } else {
            throw new BookRequestNotFoundException();
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public BookRequestDTO declineBookRequest(BookRequestDTO bookRequestDto) {
        if (bookRequestDto.getState() != BookRequestState.NEW) {
            throw new IllegalArgumentException("BookRequest doesn't allow to be declined. Must be in state NEW, current " +bookRequestDto.getState());
        }

        final Optional<BookRequest> bookRequest = this.bookRequestRepository.findById(bookRequestDto.getId());
        if (bookRequest.isPresent()) {
            bookRequest.get().setState(BookRequestState.DECLINED);
            return mapBookRequestToBookRequestDTO(this.bookRequestRepository.saveAndFlush(bookRequest.get()));
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
