package com.game.repository;

import com.game.entity.Player;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.Transaction;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> query = session.createNativeQuery("select * from player", Player.class);
            query.setFirstResult(pageNumber*pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    @Override
    public int getAllCount() {

        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createNamedQuery("player_getAllCount", Long.class);
            return query.uniqueResult().intValue();
        }
      }

    @Override
    public Player save(Player player) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
        }

    @Override
    public Player update(Player player) {

        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
            return player;
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
        }

    @Override
    public Optional<Player> findById(long id) {

        try (Session session = sessionFactory.openSession()) {
            Query<Player> query = session.createQuery("from Player where id = :idPlayer", Player.class);
            query.setParameter("idPlayer", id);
            return Optional.of(query.uniqueResult());
        }
    }

    @Override
    public void delete(Player player) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(player);
            transaction.commit();
        } catch (Exception ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}