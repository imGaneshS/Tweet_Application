package com.tweetapp.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tweetapp.model.Tweet;
import com.tweetapp.model.Users;
import com.tweetapp.repository.TweetAppRepository;

@Service
public class TweetService {

	private Logger logger = LoggerFactory.getLogger(TweetService.class);
			
	@Autowired
	private TweetAppRepository tweetRepo;
	
	@Autowired
	private KafkaTemplate kafkaTemplate;

	public String registerUser(Users user) {
		List<Tweet> tweetList = new ArrayList<Tweet>();
		user.setStatus(false);
		user.setTweets(tweetList);
		tweetRepo.save(user);
		return "User Created";
	}

	public List<Users> findAllUsers() {
		List<Users> allUsers = tweetRepo.findAll();
		return allUsers;
	}

	public Users userLogin(String email, String password) {
		Users userMatch = tweetRepo.findByEmail(email);

		// Handle Exception for Username not found
		if ((userMatch.getEmail().equals(null)) || (userMatch.getPassword().equals(null))) {
			System.out.println("No Users Found!");
			return null;
		}

		if (userMatch.getPassword().equals(password)) {
			userMatch.setStatus(true);
			tweetRepo.save(userMatch);

			logger.info("Login Successful");
			return userMatch;
		} else {
			logger.info("Invalid Credentials");
			return null;
		}

	}

	public String forgotPassword(String userName, String password) {

		Users usersMatch = tweetRepo.findByUsername(userName);

		if (usersMatch.getUsername().equals(null)) {
			System.out.println("No Users Found!");
			return "NoUsersFound";
		}

		if (password.equals(null)) {
			System.out.println("Password is Empty !");
			return "EmptyPassword";
		}

		if (usersMatch.getStatus()) {
			usersMatch.setPassword(password);
			tweetRepo.save(usersMatch);
			return "PwdChngd";
		} else {
			return "NeedLogin";
		}

	}

	public Users searchByUserName(String username) {

		Users usersMatch = tweetRepo.findByUsername(username);

		if (usersMatch.get_id().equals(null)) {
			return null;
		} else {
			return usersMatch;
		}
	}
	
	public String addTweet(String username, String message) {

		Users usersMatch = searchByUserName(username);
		List<Tweet> tweetList = retrieveTweetsByUsername(username);
		
		String kafkaMessage = publishMessage(kafkaTemplate, message);
//		String messageRcvd = KafkaListener.Listener( );
//		System.out.println("Listener from Service: "+messageRcvd);
		
		Tweet tweetPost = new Tweet();
		
		if ( !(usersMatch.get_id().equals(null)) && usersMatch.getStatus() ) {
			
			if(tweetList==null) {	
				tweetPost.setTweetId(1);			
			} else {
				tweetPost.setTweetId(tweetList.size() + 1);
				
			}
			
			tweetPost.setMessage(message);
			tweetPost.setLikes(0);
			tweetPost.setDislikes(0);
			tweetPost.setReplyTweet("");
			tweetList.add(tweetPost);
			
			usersMatch.setTweets(tweetList);
			tweetRepo.save(usersMatch);
			
			return "NewTweet";
		} else {
			return null;
		}

	}

	public List<Tweet> retrieveTweetsByUsername(String username) {

		Users usersMatch = searchByUserName(username);

		if (!(usersMatch.get_id().equals(null))) {
			List<Tweet> tweetList = usersMatch.getTweets();
			return tweetList;
		} else {
			return null;
		}
	}

	public String updateTweet(String message, String username, int id) {
		
		Users usersMatch = searchByUserName(username);
		List<Tweet> tweetList = retrieveTweetsByUsername(username);
		
		if(!(tweetList.isEmpty())) {
			for(Tweet tweets : tweetList) {
				if(tweets.getTweetId() == id) {
					tweets.setMessage(message);
				}
			}
			
			usersMatch.setTweets(tweetList);
			tweetRepo.save(usersMatch);
			return "TweetUpdated";
		} else {
			return "NoListFound";
		}
		
	}

	public String deleteTweet(Users usersMatch, List<Tweet> tweetList, int id) {
		
		for(Tweet tweets : tweetList) {
			if(tweets.getTweetId() == id) {
				tweetList.remove(tweets);
				usersMatch.setTweets(tweetList);
				tweetRepo.save(usersMatch);
				return "TweetDeleted";
			} else {
				return "TweetNotFound";
			}
		}
		return null;
		
	}

	public Users getLoggedInUser() {
		List<Users> userMatch = findAllUsers();
		
		for(Users user: userMatch) {
			if(user.getStatus()) {
				return user;
			} 
		}
		
		return null;
	}

	public void logoutUser(String username) {
		
		Users usersMatch = searchByUserName(username);
		
		if(usersMatch!=null) {
			usersMatch.setStatus(false);
			tweetRepo.save(usersMatch);
		}
		
		
		
	}

	public String replyTweet(String username, Integer id, String message) {
		Users usersMatch = searchByUserName(username);
		List<Tweet> tweetList = retrieveTweetsByUsername(username);
		
		if(!(tweetList.isEmpty())) {
			for(Tweet tweets : tweetList) {
				if(tweets.getTweetId() == id) {
					tweets.setReplyTweet(message);
				}
			}
			
			usersMatch.setTweets(tweetList);
			tweetRepo.save(usersMatch);
			return "RepliedTweet";
		} else {
			return null;
		}
	}

	public String likeTweet(String username, Integer id) {
		Users usersMatch = searchByUserName(username);
		List<Tweet> tweetList = retrieveTweetsByUsername(username);
		
		if(!(tweetList.isEmpty())) {
			for(Tweet tweets : tweetList) {
				if(tweets.getTweetId() == id) {
					tweets.setLikes(tweets.getLikes()+1);
				}
			}
			
			usersMatch.setTweets(tweetList);
			tweetRepo.save(usersMatch);
			return "LikeUpdated";
		} else {
			return null;
		}
	}

	public String dislikeTweet(String username, Integer id) {
		Users usersMatch = searchByUserName(username);
		List<Tweet> tweetList = retrieveTweetsByUsername(username);
		
		if(!(tweetList.isEmpty())) {
			for(Tweet tweets : tweetList) {
				if(tweets.getTweetId() == id) {
					tweets.setDislikes(tweets.getDislikes()+1);
				}
			}
			
			usersMatch.setTweets(tweetList);
			tweetRepo.save(usersMatch);
			return "DislikeUpdated";
		} else {
			return null;
		}
	}

	public String publishMessage(KafkaTemplate<String, String> kafkaTemplate, String message) {
		kafkaTemplate.send("newTweet", message);
		return message; 
	}
}
