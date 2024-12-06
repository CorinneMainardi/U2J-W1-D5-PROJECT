package it.epicode;

import it.epicode.dao.CatalogDAO;
import it.epicode.dao.LoanDAO;
import it.epicode.dao.UserDAO;
import it.epicode.entities.*;
import it.epicode.enums.Periodicita;
import it.epicode.runner.Runner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Application {


    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();
        final Scanner scanner = new Scanner(System.in);

        UserDAO userDAO = new UserDAO(em);
        CatalogDAO catalogDAO = new CatalogDAO(em);
        LoanDAO loanDAO = new LoanDAO(em);

        Runner.run(em);


        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Aggiungi un libro");
            System.out.println("2. Aggiungi una rivista");
            System.out.println("3. Ricerca per ISBN");
            System.out.println("4. Ricerca per anno di pubblicazione");
            System.out.println("5. Ricerca per autore");
            System.out.println("6. Ricerca per titolo o parte di esso");
            System.out.println("7. Ricerca prestito per numero di tessera)");
            System.out.println("8. Ricerca utente  per numero di tessera)");
            System.out.println("9. Mostra prestiti scaduti e non restituiti");
            System.out.println("10. Rimuovi un elemento tramite ISBN)");
            System.out.println("11. Esci");
            System.out.print("Scegli un'opzione: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consuma il newline
            switch (choice) {
                case 1 -> addBook(scanner, catalogDAO);
                case 2 -> addMagazine(scanner, catalogDAO);
                case 3 -> searchByIsbn(scanner, catalogDAO);
                case 4 -> searchByPublicationYear(scanner, catalogDAO);
                case 5 -> searchByAuthor(scanner, catalogDAO);
                case 6 -> searchByTitle(scanner, catalogDAO);
                case 7 -> searchByCardNumber(scanner, loanDAO);
                case 8 -> searchByUserCard(scanner, userDAO);
                case 9 -> showExpiredLoans(scanner, loanDAO);
                case 10 -> removeItemByIsbn(scanner, catalogDAO);
                case 11 -> {
                    scanner.close();
                    em.close();
                    emf.close();
                    System.exit(0);
                }
                default -> System.out.println("Opzione non valida. Riprova.");
            }


        }
    }


    //-------------------------------------METODI DI CATALOG ---------------------------------------------

    // Metodo per aggiungere un libro
    public static void addBook(Scanner scanner, CatalogDAO catalogDAO) {
        // Crea un'istanza di Book
        Book book = new Book();

        // Richiedi i dati all'utente tramite lo scanner
        System.out.print("Inserisci il titolo del libro: ");
        book.setTitle(scanner.nextLine());

        System.out.print("Inserisci l'autore del libro: ");
        book.setAuthor(scanner.nextLine());

        System.out.print("Inserisci l'anno di pubblicazione: ");
        book.setPublicationYear(scanner.nextInt());

        System.out.print("Inserisci il numero di pagine: ");
        book.setPagesNr(scanner.nextInt());

        // Gestione dell'ISBN
        scanner.nextLine(); // Pulisce il buffer del newline
        //System.out.print("Inserisci l'ISBN del libro: ");
        //String isbnString = scanner.nextLine();
        //UUID isbn = UUID.fromString(isbnString);
        //book.setIsbn(isbn);

        // Salva il libro nel catalogo tramite il DAO
        catalogDAO.save(book);
        System.out.println("Libro aggiunto con successo!");
    }

    // Metodo per aggiungere una rivista
    public static void addMagazine(Scanner scanner, CatalogDAO catalogDAO) {
        // Crea un'istanza di Magazine
        Magazine magazine = new Magazine();

        // Richiedi i dati all'utente tramite lo scanner
        System.out.print("Inserisci il titolo della rivista: ");
        magazine.setTitle(scanner.nextLine());

        System.out.print("Inserisci l'anno di pubblicazione: ");
        magazine.setPublicationYear(scanner.nextInt());

        System.out.print("Inserisci il numero di pagine: ");
        magazine.setPagesNr(scanner.nextInt());

        // Gestione della periodicità
        scanner.nextLine();  // Pulisce il buffer del newline
        System.out.print("Inserisci la periodicità (GIORNALIERA, SETTIMANALE, MENSILE, ANNUALE): ");
        String periodicitaStr = scanner.nextLine().toUpperCase();

        // Verifica e imposta la periodicità
        try {
            Periodicita periodicita = Periodicita.valueOf(periodicitaStr);
            magazine.setPeriodicita(periodicita);
        } catch (IllegalArgumentException e) {
            System.out.println("Periodicità non valida. Impostando a MENSILE.");
            magazine.setPeriodicita(Periodicita.MENSILE); // Imposta un valore di default
        }

        // Salva la rivista nel catalogo tramite il DAO
        catalogDAO.save(magazine);
        System.out.println("Rivista aggiunta con successo!");
    }

    // Metodo per la ricerca per ISBN
    public static void searchByIsbn(Scanner scanner, CatalogDAO catalogDAO) {
        System.out.print("Inserisci l'ISBN da ricercare: ");
        String isbnString = scanner.nextLine();
        UUID isbn = UUID.fromString(isbnString);

        Catalog catalogByIsbn = catalogDAO.findByIsbn(isbn);

        if (catalogByIsbn != null) {
            System.out.println("Risultato trovato: " + catalogByIsbn.getTitle());
        } else {
            System.out.println("La ricerca non ha prodotto risultati con l'ISBN: " + isbn);
        }
    }

    //ricerca per autore
    public static void searchByAuthor(Scanner scanner, CatalogDAO catalogDAO) {
        System.out.print("Inserisci l'autore da ricercare:");
        String ricercaAutore = scanner.nextLine();
        Catalog catalogByAuthor = catalogDAO.findByAuthor(ricercaAutore);
        if (catalogByAuthor != null) {
            System.out.println("Risultato trovato per " + ricercaAutore + ": " + catalogByAuthor.getTitle());
        } else {
            System.out.println("La ricerca non ha prodotto risultati per l'autore:" + ricercaAutore);
        }
    }

    //ricerca per anno di pubblicazione
    public static void searchByPublicationYear(Scanner scanner, CatalogDAO catalogDAO) {
        System.out.print("Inserisci l'anno di pubblicazione da ricercare:");
        String publicationYearStr = scanner.nextLine();
        int publicationYear = 0;
        try {
            publicationYear = Integer.parseInt(publicationYearStr); // Prova a convertire la stringa in int
        } catch (NumberFormatException e) {
            System.out.println("L'anno di pubblicazione inserito non è valido.");
            return; // Esci dal metodo o fai altre azioni in caso di errore
        }
        Catalog catalogByPublicationYear = catalogDAO.findByPublicationYear(publicationYear);
        if (catalogByPublicationYear != null) {
            System.out.println("Risultato trovato per " + publicationYear + ": " + catalogByPublicationYear.getTitle());
        } else {
            System.out.println("La ricerca non ha prodotto risultati per l'anno di pubblicazione:" + publicationYear);
        }
    }

    //ricerca per titolo
    public static void searchByTitle(Scanner scanner, CatalogDAO catalogDAO) {
        System.out.print("Inserisci il titolo da ricercare o parte di esso:");
        String ricercaTitolo = scanner.nextLine();
        List<Catalog> catalogByTitleList = catalogDAO.findByTitle(ricercaTitolo);
        if (!catalogByTitleList.isEmpty()) {
            System.out.println("Risultati trovati per il titolo '" + ricercaTitolo + "':");
            for (Catalog catalog : catalogByTitleList) {
                System.out.println("- " + catalog.getTitle());  // Stampa ogni titolo trovato
            }
        } else {
            System.out.println("La ricerca non ha prodotto risultati per il titolo: " + ricercaTitolo);
        }
    }


    //delete
    public static void removeItemByIsbn(Scanner scanner, CatalogDAO catalogDAO) {
        // Chiedi all'utente di inserire l'ISBN
        System.out.print("Inserisci l'ISBN dell'elemento da rimuovere: ");
        String isbnString = scanner.nextLine(); // Leggi l'ISBN come stringa

        try {
            // Converti la stringa in un oggetto UUID
            UUID isbn = UUID.fromString(isbnString);

            // Chiama il metodo delete del CatalogDAO per rimuovere l'elemento
            catalogDAO.delete(isbn);
            System.out.println("Elemento con ISBN " + isbn + " rimosso con successo.");
        } catch (IllegalArgumentException e) {
            System.out.println("L'ISBN inserito non è valido.");
        }
    }

    //-------------------------------------METODI DI LOAN ---------------------------------------------
    //ricerca per numero tessera
    public static void searchByCardNumber(Scanner scanner, LoanDAO loanDAO) {
        System.out.print("Inserisci un numero di tessera da ricercare per vedere i prestiti:");
        String cardNrStr = scanner.nextLine();
        int cardNr;

        try {
            cardNr = Integer.parseInt(cardNrStr);
            System.out.println("Numero di tessera: " + cardNr);


            List<Loan> loansByUserCard = loanDAO.findLoansByUserCard(cardNr);

            if (loansByUserCard != null && !loansByUserCard.isEmpty()) {
                System.out.println("Risultati trovati per la tessera nr '" + cardNr + "':");
                for (Loan loan : loansByUserCard) {
                    System.out.println("- Titolo prestato: " + loan.getGeneralLoan().getTitle());
                    System.out.println("  Data prestito: " + loan.getStartLoan());
                    System.out.println("  Data prevista restituzione: " + loan.getExpectedReturn());
                    // Gestisci se la data effettiva di restituzione è nulla
                    if (loan.getActualReturnDate() != null) {
                        System.out.println("  Data restituzione effettiva: " + loan.getActualReturnDate());
                    } else {
                        System.out.println("  Data restituzione effettiva: Non restituito ancora.");
                    }


                }
            } else {
                System.out.println("La ricerca non ha prodotto risultati per la tessera nr: " + cardNr);
            }

        } catch (NumberFormatException e) {
            System.out.println("Errore: Inserisci un numero tessera valido.");
        }
    }

    //ricerca prestiti scaduti e non restituiti
    // Ricerca prestiti scaduti e non restituiti
    public static void showExpiredLoans(Scanner scanner, LoanDAO loanDAO) {
        System.out.println("Ricerca dei prestiti scaduti e non restituiti. Inserisci la data di ricerca nel formato YYYY-MM-DD: ");
        String todayStr = scanner.nextLine();

        try {
            // Converte la data inserita dall'utente in un oggetto LocalDate
            LocalDate today = LocalDate.parse(todayStr);

            // Chiama il metodo DAO per trovare i prestiti scaduti rispetto alla data fornita
            List<Loan> expiredLoans = loanDAO.findExpiredLoans(today);

            if (expiredLoans != null && !expiredLoans.isEmpty()) {
                System.out.println("Prestiti scaduti trovati:");
                for (Loan loan : expiredLoans) {
                    System.out.println("- Prestito ID: " + loan.getId());
                    System.out.println("  Utente: " + loan.getUser().getName() + " " + loan.getUser().getSurname());
                    System.out.println("  Titolo del catalogo prestato: " + loan.getGeneralLoan().getTitle());
                    System.out.println("  Data prestito: " + loan.getStartLoan());
                    System.out.println("  Data prevista restituzione: " + loan.getExpectedReturn());

                    // Gestisci se la data effettiva di restituzione è nulla
                    if (loan.getActualReturnDate() != null) {
                        System.out.println("  Data restituzione effettiva: " + loan.getActualReturnDate());
                    } else {
                        System.out.println("  Data restituzione effettiva: Non restituito ancora.");
                    }

                    System.out.println("--------------------------------------------------");
                }
            } else {
                System.out.println("Non sono stati trovati prestiti scaduti e non restituiti.");
            }

        } catch (Exception e) {
            System.out.println("Errore: Inserisci una data valida nel formato YYYY-MM-DD.");
            e.printStackTrace();
        }
    }

    //-------------------------------------METODI DI UTENTE ---------------------------------------------
    public static void searchByUserCard(Scanner scanner, UserDAO userDAO) {
        System.out.println("Inserisci un numero di tessera per trovare l'utente: ");
        String userCardStr = scanner.nextLine();

        try {
            int userCard = Integer.parseInt(userCardStr);
            System.out.println("Numero di tessera inserito: " + userCard);

            User user = userDAO.findUserByCard(userCard);

            if (user != null) {
                // Stampa i dettagli dell'utente
                System.out.println("Utente trovato:");
                System.out.println("  Nome: " + user.getName());
                System.out.println("  Cognome: " + user.getSurname());
                System.out.println("  Data di nascita: " + user.getBirthDate());
                System.out.println("  Prestiti associati:");

                // Stampa i prestiti dell'utente
                List<Loan> loanList = user.getLoanList();
                if (loanList != null && !loanList.isEmpty()) {
                    for (Loan loan : loanList) {
                        System.out.println("    - Prestito ID: " + loan.getId());
                        System.out.println("      Titolo del catalogo: " + loan.getGeneralLoan().getTitle());
                        System.out.println("      Data inizio prestito: " + loan.getStartLoan());
                        System.out.println("      Data prevista restituzione: " + loan.getExpectedReturn());

                        // Stampa la data di restituzione effettiva solo se il prestito è stato restituito
                        if (loan.getActualReturnDate() != null) {
                            System.out.println("      Data restituzione effettiva: " + loan.getActualReturnDate());
                        } else {
                            System.out.println("      Prestito non restituito ancora.");
                        }

                        System.out.println("--------------------------------------------------");
                    }
                } else {
                    System.out.println("  Nessun prestito trovato per questo utente.");
                }
            } else {
                System.out.println("Nessun utente trovato con il numero di tessera: " + userCard);
            }

        } catch (NumberFormatException e) {
            System.out.println("Errore: Inserisci un numero tessera valido.");
        }
    }
}

