package com.github.mshin.jpa.dao.api.spring;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.github.mshin.jpa.dao.api.base.BaseDao;

/**
 * @author MunChul Shin
 * @param <T>
 */
/*
 * @Transactional allows methods that must happen in a transaction to work, such
 * as create, update, delete
 */
@Transactional
public abstract class AbstractDao<T> implements BaseDao<T> {

    protected Class<T> entityClass;

    private static final String CLASS_NAME = AbstractDao.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(CLASS_NAME);

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public <U extends T> void persist(U entity) {

        entityManager.persist(entity);
        entityManager.flush();

        LOGGER.debug("{}.persist()", CLASS_NAME);
    }

    @Override
    public <U extends T> List<U> update(Iterable<U> entities) {

        List<U> list = new ArrayList<U>();
        for (U u : entities) {
            list.add(entityManager.merge(u));
        }
        entityManager.flush();

        LOGGER.debug("{}.update( entities )", CLASS_NAME);
        return list;
    }

    @Override
    public <U extends T> U update(U entity) {

        U updated = entityManager.merge(entity);
        entityManager.flush();

        LOGGER.debug("{}.update( entity )", CLASS_NAME);

        return updated;
    }

    @Override
    public boolean exists(Object id) {

        boolean result = false;
        if (null != id)
            result = entityManager.find(this.entityClass, id) == null ? false : true;

        LOGGER.debug("{}.exists( {} )", CLASS_NAME, id);

        return result;
    }

    @Override
    public T findById(Object id) {

        T result = entityManager.find(this.entityClass, id);

        LOGGER.debug("{}.findById( {} )", CLASS_NAME, id);

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {

        Query query = entityManager.createQuery("from " + this.entityClass.getName());
        LOGGER.debug("findAll query: {}", query.toString());

        List<T> results = (List<T>) query.getResultList();

        LOGGER.debug("{}.findAll()", CLASS_NAME);

        return results;
    }

    @Override
    public List<T> findAll(Iterable<Object> ids) {

        List<T> list = new ArrayList<T>();
        for (Object id : ids) {
            if (id != null)
                list.add(findById(id));
        }

        LOGGER.debug("{}.findAll( ids )", CLASS_NAME);

        return list;
    }

    @Override
    public int count() {

        Query query = entityManager.createQuery("select count(*) from " + this.entityClass.getName());
        LOGGER.debug("count query: {}", query.toString());

        int result = ((Long) query.getSingleResult()).intValue();

        LOGGER.debug("{}.count()", CLASS_NAME);

        return result;
    }

    @Override
    public void deleteById(Object id) {

        T found = findById(id);
        if (null != found)
            delete(found);
        else
            LOGGER.warn("Did not delete entity {} with id {} because no entity with that id was found in the DB.",
                    this.entityClass.getSimpleName(), id);

        LOGGER.debug("{}.deleteById( {} )", CLASS_NAME, id);
    }

    @Override
    public void delete(T entity) {

        entityManager.remove(entity);

        LOGGER.debug("{}.delete( entity )", CLASS_NAME);
    }

    @Override
    public void delete(Iterable<? extends T> entities) {

        for (T t : entities)
            if (t != null)
                delete(t);

        LOGGER.debug("{}.delete( entities )", CLASS_NAME);
    }

    @Override
    public void deleteAll() {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> query = builder.createCriteriaDelete(entityClass);
        query.from(entityClass);
        entityManager.createQuery(query).executeUpdate();

        LOGGER.debug("{}.deleteAll()", CLASS_NAME);
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
