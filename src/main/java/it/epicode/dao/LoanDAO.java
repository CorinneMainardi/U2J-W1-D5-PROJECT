package it.epicode.dao;

import it.epicode.entities.Loan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoanDAO {

    private EntityManager em;
    public LoanDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Loan loan){
        em.getTransaction().begin();
        em.persist (loan);
        em.getTransaction().commit();
    }
    public List<Loan> findLoansByUserCard(int cardNr) {
        try {

            TypedQuery<Loan> query = em.createQuery(
                    "SELECT l FROM Loan l WHERE l.user.cardNr = :cardNr AND l.expectedReturn IS NULL OR l.expectedReturn > :today", Loan.class);

            query.setParameter("cardNr", cardNr);
            query.setParameter("today", LocalDate.now());

            return query.getResultList();
        } catch (NoResultException e) {
            System.out.println("Nessun elemento trovato per la tessera nr : " + cardNr);
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dei prestiti con numero tessera: " + cardNr, e);
        }
    }
    public List<Loan> findExpiredLoans(LocalDate referenceDate) {
        try {
            TypedQuery<Loan> query = em.createQuery(
                    "SELECT l FROM Loan l WHERE l.expectedReturn < :referenceDate AND l.generalLending IS NOT NULL", Loan.class
            );
            query.setParameter("referenceDate", referenceDate);

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dei prestiti scaduti", e);
        }
    }

}
