package ru.practicum.shareit.booking.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatedBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.InputDataException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final User mockOwner = User.builder()
            .id(1)
            .name("OwnerOleg")
            .email("OwnerOleg@ya.com")
            .build();
    private final User mockBooker = User.builder()
            .id(2)
            .name("BookerMike")
            .email("BookerMike@ya.com")
            .build();
    private final Item mockItem = Item.builder()
            .id(1)
            .name("Item")
            .description("ItemDescription")
            .owner(mockOwner)
            .build();

    private final Booking mockBooking = Booking.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(2))
            .item(mockItem)
            .booker(mockBooker)
            .status(BookingStatus.WAITING)
            .build();

    private final CreatedBookingDto mockCreatedBookingDto = CreatedBookingDto.builder()
            .id(1)
            .start(LocalDateTime.now().plusDays(3))
            .end(LocalDateTime.now().plusDays(4))
            .itemId(1)
            .build();

    private final BookingDto mockBookingDto = BookingDto.builder()
            .id(1).start(LocalDateTime.now().plusDays(5))
            .end(LocalDateTime.now().plusDays(6))
            .item(BookingDto.Item.builder().id(1).build())
            .booker(BookingDto.User.builder().id(1).build())
            .status(BookingStatus.WAITING)
            .build();


    @Test
    void testCreateItem() throws Exception {
        when(bookingService.createBooking(anyInt(), any())).thenReturn(mockBooking);
        doReturn(mockCreatedBookingDto).when(bookingMapper).toCreatedBookingDto(any());

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(mockCreatedBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockCreatedBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(mockCreatedBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(mockCreatedBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.itemId", is(mockCreatedBookingDto.getItemId()), Integer.class));
    }

    @Test
    void testSetApproved() throws Exception {
        when(bookingService.setApproved(anyInt(), anyInt(), anyBoolean())).thenReturn(mockBooking);
        doReturn(mockBookingDto).when(bookingMapper).toBookingDto(any());

        mockMvc.perform(getContentWithPatchMethod())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(mockBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(mockBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(mockBookingDto.getStatus().toString())));
    }

    @Test
    void testSetApprovedWithoutRights() throws Exception {
        when(bookingService.setApproved(anyInt(), anyInt(), anyBoolean()))
                .thenThrow(new InputDataException("Пользователь не может менять статус"));

        mockMvc.perform(getContentWithPatchMethod())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testFindItemById() throws Exception {
        when(bookingService.findBookingById(anyInt(), anyInt())).thenReturn(mockBooking);

        doReturn(mockBookingDto).when(bookingMapper).toBookingDto(any());
        mockMvc.perform(getContentWithGetMethod("/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(mockBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.end", is(mockBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.item.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(mockBookingDto.getStatus().toString())));
    }

    @Test
    void testFindAllByBookerId() throws Exception {
        when(bookingService.findAllByBookerId(anyInt(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(mockBooking));
        doReturn(mockBookingDto).when(bookingMapper).toBookingDto(any());
        mockMvc.perform(getContentWithGetMethod("/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(mockBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(mockBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].booker.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(mockBookingDto.getStatus().toString())));
    }

    @Test
    void testFindAllByBookerIdStateIsUnsupported() throws Exception {
        when(bookingService.findAllByBookerId(anyInt(), any(String.class), anyInt(), anyInt()))
                .thenAnswer(result -> {
                    boolean flag = false;
                    for (BookingState state : BookingState.values()) {
                        if (state.name().equals(result)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        throw new ValidationException("Unknown state: " + result);
                    } else {
                        return List.of(mockBooking);
                    }
                });

        mockMvc.perform((getContentWithGetMethod("/bookings"))
                        .queryParam("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testFindAllByOwnerId() throws Exception {
        when(bookingService.findAllByOwnerId(anyInt(), any(String.class), anyInt(), anyInt()))
                .thenReturn(List.of(mockBooking));
        doReturn(mockBookingDto).when(bookingMapper).toBookingDto(any());
        mockMvc.perform(getContentWithGetMethod("/bookings/owner"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].start", is(mockBookingDto.getStart()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].end", is(mockBookingDto.getEnd()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))))
                .andExpect(jsonPath("$.[0].item.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].booker.id", is(mockBookingDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(mockBookingDto.getStatus().toString())));
    }

    @Test
    void testFindAllByOwnerIdStateIsUnsupported() throws Exception {
        when(bookingService.findAllByOwnerId(anyInt(), any(String.class), anyInt(), anyInt()))
                .thenAnswer(result -> {
                    boolean flag = false;
                    for (BookingState state : BookingState.values()) {
                        if (state.name().equals(result)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        throw new ValidationException("Unknown state: " + result);
                    } else {
                    return List.of(mockBooking);
                }
                });

        mockMvc.perform(getContentWithGetMethod("/bookings/owner")
                        .queryParam("state", "UNSUPPORTED_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    MockHttpServletRequestBuilder getContentWithPatchMethod() throws JsonProcessingException {
        return patch("/bookings/1")
                .content(objectMapper.writeValueAsString(mockBookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER_USER_ID, 1)
                .queryParam("approved", "true");
    }

    MockHttpServletRequestBuilder getContentWithGetMethod(String url) throws JsonProcessingException {
        return get(url)
                .content(objectMapper.writeValueAsString(mockBookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER_USER_ID, 1);
    }
}