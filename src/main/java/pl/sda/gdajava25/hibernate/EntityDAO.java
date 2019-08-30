package pl.sda.gdajava25.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import pl.sda.gdajava25.model.IBaseEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public class EntityDAO {

    public <T extends IBaseEntity> void saveOrUpdate(T t) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.saveOrUpdate(t);

            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    public <T extends IBaseEntity> List<T> getAll(Class<T> tClass) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> criteriaQuery = cb.createQuery(tClass);
            Root<T> root = criteriaQuery.from(tClass);
            criteriaQuery.select(root);
            return session.createQuery(criteriaQuery).list();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T extends IBaseEntity> Optional<T> getById(Class<T> tClass, Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            T t = session.get(tClass, id);

            return Optional.ofNullable(t);
        }
    }

    public <T extends IBaseEntity> void delete(Class<T> tClass, Long id) {
        Optional<T> optionalT = getById(tClass, id);

        if (optionalT.isPresent()) {
            deleteByObject(optionalT.get());
        } else {
            System.err.println("Nie udało sie odnaleźć instancji");
        }
    }

    private <T extends IBaseEntity> void deleteByObject(T t) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.delete(t);

            transaction.commit();
        }
    }
}
