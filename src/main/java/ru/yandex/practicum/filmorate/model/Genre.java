package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
public class Genre {

    private long id;

    private String name;
}
