package com.tweetapp.model;

public class Tweet {

	private Integer tweetId;
	private String message;
	private Integer likes;
	private Integer dislikes;
	private String replyTweet;

	public Integer getTweetId() {
		return tweetId;
	}

	public void setTweetId(Integer tweetId) {
		this.tweetId = tweetId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	public Integer getDislikes() {
		return dislikes;
	}

	public void setDislikes(Integer dislikes) {
		this.dislikes = dislikes;
	}

	public String getReplyTweet() {
		return replyTweet;
	}

	public void setReplyTweet(String replyTweet) {
		this.replyTweet = replyTweet;
	}

}
