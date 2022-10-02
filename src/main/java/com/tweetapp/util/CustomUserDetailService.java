package com.tweetapp.util;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tweetapp.model.Users;
import com.tweetapp.repository.TweetAppRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private TweetAppRepository tweetRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Users user = tweetRepo.findByEmail(email);
		return new User(user.getEmail(),user.getPassword(),new ArrayList<>());
	}

}
