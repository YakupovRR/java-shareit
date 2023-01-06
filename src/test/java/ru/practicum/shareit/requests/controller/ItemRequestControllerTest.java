package ru.practicum.shareit.requests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private ItemRequestMapper itemRequestMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private final User mockUser = User.builder().id(1).name("User1").email("FirstUser@ya.ru").build();
    private final ItemRequestDto.User mockUserDto = ItemRequestDto.User.builder().id(1).name("UserDTO")
            .email("UserDTO@ya.ru").build();
    private final ItemRequestDto mockItemRequestDto = ItemRequestDto.builder().id(1).description("Description")
            .requester(mockUserDto).created(LocalDateTime.now()).build();
    private final ItemRequest mockItemRequest = ItemRequest.builder().id(1).description("Description")
            .requester(mockUser).created(LocalDateTime.now()).build();


    @Test
    void testCreateItemRequest() throws Exception {
        when(itemRequestService.createRequest(any(Integer.class), any())).thenReturn(mockItemRequest);
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(getContentWithGPostMethod("/requests"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(mockItemRequestDto.getRequester()
                        .getId()), Integer.class))
                .andExpect(jsonPath("$.requester.name", is(mockItemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(mockItemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testCreateItemRequestFailedValidation() throws Exception {
        when(itemRequestService.createRequest(any(Integer.class), any()))
                .thenThrow(new ValidationException("Отсутствует описание запрашиваемой вещи"));
        mockMvc.perform(getContentWithGPostMethod("/requests"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Отсутствует описание запрашиваемой вещи")));
    }

    @Test
    void testFindItemRequestsByUserId() throws Exception {
        when(itemRequestService.getAllRequestByUserId(any(Integer.class))).thenReturn(List.of(mockItemRequest));
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(mockItemRequestDto.getRequester()
                        .getId()), Integer.class))
                .andExpect(jsonPath("$.[0].requester.name", is(mockItemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(mockItemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testFindAllItemRequest() throws Exception {
        when(itemRequestService.getAllRequest(any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(mockItemRequest));
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id", is(mockItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.[0].requester.id", is(mockItemRequestDto.getRequester()
                        .getId()), Integer.class))
                .andExpect(jsonPath("$.[0].requester.name", is(mockItemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.[0].requester.email", is(mockItemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.[0].created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    @Test
    void testFindItemRequestById() throws Exception {
        when(itemRequestService.getRequestById(any(Integer.class), any(Integer.class))).thenReturn(mockItemRequest);
        doReturn(mockItemRequestDto).when(itemRequestMapper).toItemRequestDto(any());

        mockMvc.perform(get("/requests/1")
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(mockItemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(mockItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(mockItemRequestDto.getRequester()
                        .getId()), Integer.class))
                .andExpect(jsonPath("$.requester.name", is(mockItemRequestDto.getRequester().getName())))
                .andExpect(jsonPath("$.requester.email", is(mockItemRequestDto.getRequester().getEmail())))
                .andExpect(jsonPath("$.created", is(mockItemRequestDto.getCreated()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))));
    }

    MockHttpServletRequestBuilder getContentWithGPostMethod(String url) throws JsonProcessingException {
        return post(url)
                .content(objectMapper.writeValueAsString(mockItemRequestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER_USER_ID, 1);
    }
}