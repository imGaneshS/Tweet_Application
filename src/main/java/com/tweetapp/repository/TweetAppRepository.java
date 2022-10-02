package com.tweetapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.model.Users;

@Repository
public interface TweetAppRepository extends MongoRepository<Users, Integer> {

	Users findByEmail(String email);

	Users findByUsername(String userName);

	
}
