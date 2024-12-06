package it.epicode.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "Catalog")
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    private UUID isbn;
    private String title;
    private int publicationYear;
    private int pagesNr;
}
