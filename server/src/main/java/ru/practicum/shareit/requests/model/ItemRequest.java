package ru.practicum.shareit.requests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * // TODO .
 */
@Data
@Entity
@Table(name = "item_requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    private LocalDateTime created;
    @OneToMany()
    @JoinColumn(name = "request_id")
    private Collection<Item> items;
}
