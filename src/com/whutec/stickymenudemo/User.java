package com.whutec.stickymenudemo;

import java.util.ArrayList;

/**
 * @author Jimmy
 * @date 2014-4-30 下午6:59:24 
 * @description 实体类
 */
public class User{
	
	public int id;
	
	public String name;
	
	public static ArrayList<User> makeUers(User lastUser, int type){
		ArrayList<User> users = new ArrayList<User>();
		if(lastUser == null){
			for(int i=0; i<10; i++){
				User user = makeUser(i, type);
				users.add(user);
			}
		}else{
			for(int i=lastUser.id+1; i<lastUser.id+11; i++){
				User user = makeUser(i, type);
				users.add(user);
			}
		}
		return users;
	}
	
	public static User makeUser(int startId, int type){
		User user = new User();
		user.id = startId;
		if(type == 0){
			user.name = "Red Rose";
		}else if(type == 1){
			user.name = "Jimmy";
		}
		return user;
	}
	
}