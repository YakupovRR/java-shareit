package ru.practicum.shareit.trait;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PageTool {
    default Pageable getPage(int from, int size, String sort, Sort.Direction direction) {
        Sort sortById = Sort.by(direction, sort);
        return PageRequest.of((from / size), size, sortById);
    }
}
