package com.tweetapp.config;

import org.springframework.stereotype.Component;

@Component
public class KafkaListener {
	
	public static String messageReceived = "";
	
	@org.springframework.kafka.annotation.KafkaListener(topics = "newTweet", groupId = "tweet")
	public static String Listener(String data){
		messageReceived="";
		System.out.println("Listener Received: "+data);
		messageReceived = data;
		return messageReceived;
	}
	
	public static String getMessage() {
		return messageReceived;
	}

}
