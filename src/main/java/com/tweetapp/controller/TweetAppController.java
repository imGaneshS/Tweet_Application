package com.tweetapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.model.Tweet;
import com.tweetapp.model.Users;
import com.tweetapp.repository.TweetAppConstants;
import com.tweetapp.service.TweetService;
import com.tweetapp.util.JwtUtil;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetAppController {

	private Logger logger = LoggerFactory.getLogger(TweetAppController.class);

	@Autowired
	private TweetService tweetService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody Users user) {

		String userCreated = tweetService.registerUser(user);
		return new ResponseEntity<>(userCreated, HttpStatus.CREATED);
	}

	@GetMapping("/login")
	public String loginUser(@RequestParam String email, @RequestParam String password) throws Exception {
		
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		} catch (Exception ex) {
			throw new Exception("Invalid username/password");
		}
		String token =  jwtUtil.generateToken(email);
		tweetService.userLogin(email, password);
		System.out.println("Token: "+ token);
		return token;
	}

	@PostMapping("/{username}/forgot")
	public ResponseEntity<String> forgotPassword(@PathVariable String username, @RequestParam String password) {

		String ChangePasswordStatus = tweetService.forgotPassword(username, password);

		if (ChangePasswordStatus.equals("PwdChngd")) {
			return new ResponseEntity<>(TweetAppConstants.PwdSuccess, HttpStatus.OK);
		} else if (ChangePasswordStatus.equals("NeedLogin")) {
			return new ResponseEntity<>("Not Authorized !", HttpStatus.UNAUTHORIZED);
		} else if (ChangePasswordStatus.equals("EmptyPassword")) {
			return new ResponseEntity<>("Password is Empty !", HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>("No Users Found !", HttpStatus.NOT_FOUND);
		}

	}

	@GetMapping("/search")
	public Users searchByUserName(@RequestParam String username) {

		Users usersMatch = tweetService.searchByUserName(username);

		if (usersMatch == null) {
			return null;
		}

		return usersMatch;
	}

	@GetMapping("/users/all")
	public List<Users> findAllUsers() {

		List<Users> allUsersList = tweetService.findAllUsers();

		if (allUsersList.isEmpty()) {
			return null;
		}

		logger.info(allUsersList.toString());
		return allUsersList;

	}

	@PostMapping("/{username}/add")
	public String addTweet(@PathVariable String username, @RequestParam String message) {

		String postTweetStatus = tweetService.addTweet(username, message);

		if (postTweetStatus == null) {
			return null;
		}

		return "Tweet Posted Successfully";
	}

	@GetMapping("/{username}")
	public ResponseEntity<List<Tweet>> retrieveTweetsByUsername(@PathVariable String username) {
		List<Tweet> tweetList = tweetService.retrieveTweetsByUsername(username);

		if (tweetList.isEmpty()) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(tweetList, HttpStatus.FOUND);
	}

	@PutMapping("/{username}/update/{id}")
	public ResponseEntity<String> updateTweet(@RequestParam String message, @PathVariable String username,
			@PathVariable int id) {
		String updateTweetStatus = tweetService.updateTweet(message, username, id);

		if (updateTweetStatus == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>("Tweet Updated Successfully", HttpStatus.OK);
	}

	@DeleteMapping("/{username}/delete/{id}")
	public ResponseEntity<String> deleteTweet(@PathVariable String username, @PathVariable int id) {
		Users usersMatch = tweetService.searchByUserName(username);

		if (!(usersMatch.get_id().equals(null))) {
			List<Tweet> tweetList = tweetService.retrieveTweetsByUsername(username);

			if (!(tweetList.isEmpty())) {
				String deleteStatus = tweetService.deleteTweet(usersMatch, tweetList, id);

				if (deleteStatus.equals("TweetDeleted")) {
					return new ResponseEntity<>("Tweet Deleted Successfully", HttpStatus.OK);
				} else if (deleteStatus.equals("TweetNotFound")) {
					return new ResponseEntity<>("Tweet Not Found", HttpStatus.NOT_FOUND);
				}

			} else {
				return new ResponseEntity<>("Tweet Not Found", HttpStatus.NOT_FOUND);
			}
		} else {
			return new ResponseEntity<>("No Users Found", HttpStatus.NOT_FOUND);
		}
		return null;

	}

	@GetMapping("/userLoggedIn")
	public Users getLoggedInUser() {
		Users userLoggedIn = tweetService.getLoggedInUser();
		return userLoggedIn;
	}

	@GetMapping("/logout")
	public void logout(@RequestParam String username) {
		tweetService.logoutUser(username);
	}

	@PostMapping("/{username}/reply/{id}")
	public ResponseEntity<String> replyTweet(@PathVariable String username, @PathVariable Integer id,
			@RequestParam String message) {
		String replyStatus = tweetService.replyTweet(username, id, message);

		if (replyStatus == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>("Tweet Updated Successfully", HttpStatus.OK);
	}

	@PutMapping("/{username}/like/{id}")
	public ResponseEntity<String> likeTweet(@PathVariable String username, @PathVariable Integer id) {
		String likeStatus = tweetService.likeTweet(username, id);

		if (likeStatus == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>("Tweet Updated Successfully", HttpStatus.OK);
	}

	@PutMapping("/{username}/dislike/{id}")
	public ResponseEntity<String> dislikeTweet(@PathVariable String username, @PathVariable Integer id) {
		String dislikeStatus = tweetService.dislikeTweet(username, id);

		if (dislikeStatus == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>("Tweet Updated Successfully", HttpStatus.OK);
	}

}
