package com.ml.db;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDB implements IBaseDB {

	MongoTemplate mongoTemplate;
	
	public MongoDB(Properties props) {
		try {
			String host = props.getProperty("mongo.host");
			int port = Integer.valueOf(props.getProperty("mongo.port"));
			String db = props.getProperty("mongo.db");
			String user = props.getProperty("mongo.user");
			String password = props.getProperty("mongo.password");
			
			UserCredentials userCredentials = new UserCredentials(user, password);
			Mongo mongo = new Mongo(host, port);
			mongoTemplate = new MongoTemplate(mongo, db, userCredentials);
			
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
		} catch (MongoException e) {
			System.out.println(e.toString());
		}
	}
	
	
	public MongoDB(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void save(Object entity) {
		mongoTemplate.save(entity);
	}

	@Override
	public void save(Object entity, String collectionName) {
		mongoTemplate.save(entity, collectionName);
	}
	
	@Override
	public void insert(Collection<? extends Object> batchToSave, String collectionName) {
		mongoTemplate.insert(batchToSave, collectionName);
	}

	@Override
	public void update(Object entity) {
		mongoTemplate.save(entity);
	}

	@Override
	public void update(Object entity, String collectionName) {
		mongoTemplate.save(entity, collectionName);
	}

	@Override
	public void delete(Object entity) {
		mongoTemplate.remove(entity);
	}

	@Override
	public void delete(Object entity, String collectionName) {
		mongoTemplate.remove(entity, collectionName);
		
	}
	
	@Override
	public void delete(Query query, String collectionName) {
		mongoTemplate.remove(query, collectionName);
	}
	
	@Override
	public void drop(String collectionName) {
		mongoTemplate.dropCollection(collectionName);
	}

	@Override
	public <T> List<T> find(Object query, Object entity) {
		return mongoTemplate.find((Query)query, (Class<T>) entity);
	}

	@Override
	public <T> List<T> find(Object query, Object entity, String collectionName) {
		return mongoTemplate.find((Query)query, (Class<T>) entity, collectionName);
	}

	@Override
	public <T> T findOne(Object query, Object entity) {
		return mongoTemplate.findOne((Query)query, (Class<T>) entity);
	}

	@Override
	public <T> T findOne(Object query, Object entity, String collectionName) {
		return mongoTemplate.findOne((Query)query, (Class<T>) entity, collectionName);
	}

	@Override
	public <T> List<T> findAll(Object entity) {
		return mongoTemplate.findAll((Class<T>) entity);
	}

	@Override
	public <T> List<T> findAll(Object entity, String collectionName) {
		return mongoTemplate.findAll((Class<T>) entity, collectionName);
	}

	@Override
	public long count(Object query, String collectionName) {
		return mongoTemplate.count((Query) query, collectionName);
	}
	
	public MongoTemplate getMongoTemplate() {
		
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}


}
