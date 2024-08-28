package ru.practicum.shareit.user.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
}
