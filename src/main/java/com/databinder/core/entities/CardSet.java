package com.databinder.core.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sets")
public class CardSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private List<Printing> printings;
}