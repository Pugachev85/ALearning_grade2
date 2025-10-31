package com.ALearning_grade2.dao;

import com.ALearning_grade2.entity.UserEntity;
import com.ALearning_grade2.exception.UserServiceException;
import com.ALearning_grade2.factory.HibernateFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса UserDao для работы с пользователями в базе данных.
 * Использует Hibernate для выполнения CRUD операций.
 */
public class UserDaoImpl implements UserDao {

    private Session openSession() {
        return HibernateFactory.openSession();
    }

    @Override
    public void create(UserEntity user) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        try (Session session = openSession()) {
            UserEntity user = session.find(UserEntity.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при поиске пользователя по ID", e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        try (Session session = openSession()) {
            Query<UserEntity> query = session.createQuery("FROM UserEntity", UserEntity.class);
            return query.list();
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при получении всех пользователей", e);
        }
    }

    @Override
    public void update(UserEntity user) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(user);
            tx.commit();
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при обновлении пользователя", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            UserEntity user = session.find(UserEntity.class, id);
            if (user != null) {
                session.remove(user);
                tx.commit();
                return true;
            }

        } catch (Exception e) {
            throw new UserServiceException("Ошибка при удалении пользователя", e);
        }
        return false;
    }
}
