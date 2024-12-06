package it.epicode.entities;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Book extends Catalog {
    private String author;
    private String genre;
}
