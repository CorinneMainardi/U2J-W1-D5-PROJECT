package it.epicode.runner;

import it.epicode.dao.CatalogDAO;
import it.epicode.dao.LoanDAO;
import it.epicode.dao.UserDAO;
import it.epicode.entities.*;
import it.epicode.enums.Periodicita;
import jakarta.persistence.EntityManager;

import java.util.List;
import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.ZoneId;

public class Runner {

    public static void run(EntityManager em) {
        // DAO Initialization
        UserDAO userDAO = new UserDAO(em);
        CatalogDAO catalogDAO = new CatalogDAO(em);
        LoanDAO loanDAO = new LoanDAO(em);

        // Faker Instance
        Faker faker = new Faker();

        // Creazione utenti
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setCardNr(faker.number().numberBetween(1000, 9999));
            user.setName(faker.name().firstName());
            user.setSurname(faker.name().lastName());
            user.setBirthDate(
                    faker.date().birthday(18, 65).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
            userDAO.save(user);
        }

        // Creazione catalogo (libri e riviste)
        for (int i = 0; i < 3; i++) {
            Book book = new Book();
            book.setTitle(faker.book().title());
            book.setAuthor(faker.book().author());
            book.setGenre(faker.book().genre());
            book.setPublicationYear(faker.number().numberBetween(1900, 2023));
            book.setPagesNr(faker.number().numberBetween(50, 1000));
            catalogDAO.save(book);

            Magazine magazine = new Magazine();
            magazine.setTitle(faker.lorem().sentence(3));
            magazine.setPublicationYear(faker.number().numberBetween(1900, 2023));
            magazine.setPagesNr(faker.number().numberBetween(10, 200));
            magazine.setPeriodicita(Periodicita.MENSILE); // Periodicità statica
            catalogDAO.save(magazine);
        }

        // Recupera utenti e catalogo per creare prestiti
        // Recupera utenti e catalogo per creare prestiti
        List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        List<Catalog> catalogs = em.createQuery("SELECT c FROM Catalog c", Catalog.class).getResultList();

        if (!users.isEmpty() && !catalogs.isEmpty()) {
            // Creazione prestiti
            for (int i = 0; i < 2; i++) {
                Loan loan = new Loan();
                loan.setUser(users.get(faker.random().nextInt(users.size()))); // Assegna un utente casuale
                loan.setGeneralLoan(catalogs.get(faker.random().nextInt(catalogs.size()))); // Assegna un catalogo casuale

                // Data di inizio del prestito: 60 giorni fa
                LocalDate loanStart = faker.date().past(60, java.util.concurrent.TimeUnit.DAYS)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                loan.setStartLoan(loanStart);

                // La data di restituzione prevista è sempre a 30 giorni dalla data di inizio
                loan.setExpectedReturn(loanStart.plusDays(30));

                // Imposta la restituzione effettiva in base a i
                if (i == 0) {
                    // Prestito restituito in tempo
                    loan.setActualReturnDate(loan.getExpectedReturn()); // Restituito in tempo
                } else {
                    // Prestito scaduto e non restituito
                    loan.setActualReturnDate(null); // Non restituito
                }

                // Salva il prestito nel database
                loanDAO.save(loan);
            }
        }


        System.out.println("Popolamento completato!");
    }
}
