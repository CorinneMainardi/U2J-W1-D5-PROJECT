package it.epicode.dao;

import it.epicode.entities.Loan;
import it.epicode.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class UserDAO {

    private EntityManager em;


    public UserDAO(EntityManager em) {
        this.em = em;
    }

    public void save(User user) {
        em.getTransaction().begin();
        if (user.getCardNr() == 0) {  // Se la cardNr è 0, significa che l'utente è nuovo
            em.persist(user); // Nuovo utente, lo persiste
        } else {
            em.merge(user); // Utente esistente, lo aggiorna
        }
        em.getTransaction().commit();
    }


    public User findUserByCard(int cardNr) {
        try {

            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.cardNr = :cardNr", User.class
            );
            query.setParameter("cardNr", cardNr);


            return query.getSingleResult();
        } catch (NoResultException e) {

            System.out.println("Nessun utente trovato con il numero di tessera: " + cardNr);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la ricerca dell'utente con numero di tessera: " + cardNr, e);
        }
    }
}
