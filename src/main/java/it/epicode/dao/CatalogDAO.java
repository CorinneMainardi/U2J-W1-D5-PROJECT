package it.epicode.dao;

import it.epicode.entities.Catalog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CatalogDAO {
    private EntityManager em;


    public CatalogDAO(EntityManager em) {
        this.em = em;
    }

    public void save(Catalog catalog) {
        em.getTransaction().begin();
        em.persist(catalog);
        em.getTransaction().commit();
    }
    public Catalog findByIsbn(UUID isbn){
        try {

            Catalog catalog = em.find(Catalog.class, isbn);

            if (catalog != null) {

                return catalog;
            } else {

                System.out.println("Nessun elemento trovato con ISBN: " + isbn);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dell'entità con ISBN: " + isbn, e);
        }
    }
    public Catalog findByAuthor(String author) {
        try {
            TypedQuery<Catalog> query = em.createQuery(
                    "SELECT c FROM CatalogClass c WHERE c.author = :author", Catalog.class);
            query.setParameter("author", author);
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Nessun elemento trovato per l'autore: " + author);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dell'entità con autore: " + author, e);
        }
    }
    public Catalog findByPublicationYear(int publicationYear) {
        try {
            TypedQuery<Catalog> query = em.createQuery(
                    "SELECT c FROM Catalog c WHERE c.publicationYear = :publicationYear", Catalog.class);
            query.setParameter("publicationYear", publicationYear);
            return query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Nessun elemento trovato per l'autore: " + publicationYear);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dell'entità con autore: " + publicationYear, e);
        }
    }

    //creo una lista di catalogo cosicché possa restituirmi tutti i risultati contenenti quanto indicato nella ricerca
    public List<Catalog> findByTitle(String title) {
        try {
            TypedQuery<Catalog> query = em.createQuery(
                    "SELECT c FROM Catalog c WHERE c.title = :title", Catalog.class);
            query.setParameter("title", "%" + title + "%"); //faccio una ricerca anchep er titolo parziale
            return query.getResultList();
        } catch (NoResultException e) {
            System.out.println("Nessun elemento trovato per titolo: " + title);
            return new ArrayList<>(); //lista vuota se non trov a nulla

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante il recupero dell'entità con titolo: " + title, e);
        }
    }

    public void update (Catalog catalog){
        em.getTransaction().begin();
        em.merge(catalog);
        em.getTransaction().commit();
    }
    public void delete(UUID isbn){
        em.getTransaction().begin();
        try {

            Catalog catalog = em.find(Catalog.class, isbn);

            if (catalog != null) {
                em.remove(catalog);
            } else {
                System.out.println("Elemento non trovato con ISBN: " + isbn);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback(); //rollbackk di errore
            e.printStackTrace();
        }
    }

}


