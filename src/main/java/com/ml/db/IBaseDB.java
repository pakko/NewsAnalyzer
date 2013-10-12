package com.ml.db;


import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.core.query.Query;


public interface IBaseDB {
	void save(final Object entity);
	void save(final Object entity, String collectionName);
	
	void update(final Object entity);
	void update(final Object entity, String collectionName);
	
	void delete(final Object entity);
	void delete(final Object entity, String collectionName);
	void delete(Query query, String collectionName);

	<T> List<T> find(Object query, Object entity);
	<T> List<T> find(Object query, Object entity, String collectionName);
	
	<T> T findOne(Object query, Object entity);
	<T> T findOne(Object query, Object entity, String collectionName);
	
	<T> List<T> findAll(Object entity);
	<T> List<T> findAll(Object entity, String collectionName);
	
	long count(Object query, String collectionName);
	
	void insert(Collection<? extends Object> batchToSave, String collectionName);
	void drop(String collectionName);

}
