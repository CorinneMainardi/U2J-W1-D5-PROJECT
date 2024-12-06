package it.epicode.entities;

import it.epicode.enums.Periodicita;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
@Entity

public class Magazine extends Catalog {
    @Enumerated(EnumType.STRING)
    private Periodicita periodicita;
}
