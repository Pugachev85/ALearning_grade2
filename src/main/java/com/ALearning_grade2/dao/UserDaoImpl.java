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
 * Реализация интерфейса UserDao для работы с сущностью UserEntity в базе данных.
 * Использует Hibernate для выполнения CRUD операций.
 */
public class UserDaoImpl implements UserDao {

    private Session openSession() {
        return HibernateFactory.openSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(UserEntity user) {
        if (user == null) {
            throw new UserServiceException("Пользователь не может быть null");
        }

        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new UserServiceException("Ошибка при создании пользователя", e);
            }
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при создании пользователя", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserEntity> findById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        try (Session session = openSession()) {
            UserEntity user = session.find(UserEntity.class, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при поиске пользователя по ID", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> findAll() {
        try (Session session = openSession()) {
            Query<UserEntity> query = session.createQuery("FROM UserEntity", UserEntity.class);
            return query.list();
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при получении всех пользователей", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(UserEntity user) {
        if (user == null || user.getId() == null) {
            throw new UserServiceException("Пользователь и ID пользователя не могут быть null для обновления");
        }

        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                UserEntity existingUser = session.find(UserEntity.class, user.getId());
                if (existingUser != null) {
                    session.merge(user);
                    tx.commit();
                } else {
                    tx.rollback();
                    throw new UserServiceException("Пользователь с ID " + user.getId() + " не найден");
                }
            } catch (Exception e) {
                tx.rollback();
                if (e instanceof UserServiceException) {
                    throw (UserServiceException) e;
                }
                throw new UserServiceException("Ошибка при обновлении пользователя", e);
            }
        } catch (Exception e) {
            if (e instanceof UserServiceException) {
                throw (UserServiceException) e;
            }
            throw new UserServiceException("Ошибка при обновлении пользователя", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                UserEntity user = session.find(UserEntity.class, id);
                if (user != null) {
                    session.remove(user);
                    tx.commit();
                    return true;
                } else {
                    tx.rollback();
                    return false;
                }
            } catch (Exception e) {
                tx.rollback();
                throw new UserServiceException("Ошибка при удалении пользователя", e);
            }
        } catch (Exception e) {
            throw new UserServiceException("Ошибка при удалении пользователя", e);
        }
    }
}
