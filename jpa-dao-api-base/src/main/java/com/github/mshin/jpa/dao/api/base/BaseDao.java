package com.github.mshin.jpa.dao.api.base;

/**
 * @author MunChul Shin
 */
import java.util.List;

public interface BaseDao<T> {

    public <U extends T> void persist(U entity);

    public <U extends T> List<U> update(Iterable<U> entities);

    public <U extends T> U update(U entity);

    public boolean exists(Object id);

    public <U extends T> U findById(Object id);

    public List<T> findAll();

    public List<T> findAll(Iterable<Object> ids);

    public int count();

    public void deleteById(Object id);

    public void delete(T entity);

    public void delete(Iterable<? extends T> entities);

    public void deleteAll();
}
