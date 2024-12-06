package it.epicode.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_cardNr", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "catalog_isbn", nullable = false)
    private Catalog generalLoan;
    private LocalDate startLoan;
    private LocalDate expectedReturn;


}