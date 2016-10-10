package com.wang.zhihu.entity;

public class User {

	private String username;
	private String userToken;
	private String location;
	private String business;
	private String sex;
	private String employment;
	private String position;
	private String education;
	private String url;
	private int agrees;
	private int thanks;
	private int asks;
	private int answers;
	private int posts;
	private int followees;
	private int followers;
	private String hashId;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmployment() {
		return employment;
	}
	public void setEmployment(String employment) {
		this.employment = employment;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getAgrees() {
		return agrees;
	}
	public void setAgrees(int agrees) {
		this.agrees = agrees;
	}
	public int getThanks() {
		return thanks;
	}
	public void setThanks(int thanks) {
		this.thanks = thanks;
	}
	public int getAsks() {
		return asks;
	}
	public void setAsks(int asks) {
		this.asks = asks;
	}
	public int getAnswers() {
		return answers;
	}
	public void setAnswers(int answers) {
		this.answers = answers;
	}
	public int getPosts() {
		return posts;
	}
	public void setPosts(int posts) {
		this.posts = posts;
	}
	public int getFollowees() {
		return followees;
	}
	public void setFollowees(int followees) {
		this.followees = followees;
	}
	public int getFollowers() {
		return followers;
	}
	public void setFollowers(int followers) {
		this.followers = followers;
	}
	public String getHashId() {
		return hashId;
	}
	public void setHashId(String hashId) {
		this.hashId = hashId;
	}
	@Override
	public String toString() {
		return "User {username=" + username + ", userToken=" + userToken + ", location=" + location + ", business="
				+ business + ", sex=" + sex + ", employment=" + employment + ", position=" + position + ", education="
				+ education + ", url=" + url + ", agrees=" + agrees + ", thanks=" + thanks + ", asks=" + asks
				+ ", answers=" + answers + ", posts=" + posts + ", followees=" + followees + ", followers=" + followers
				+ ", hashId=" + hashId + "}";
	}
	
}
