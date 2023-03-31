package com.travel.common.utils;

import com.travel.common.model.entity.User;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 2:45
 */
public class UserHolder {
    private static final ThreadLocal<User> tl = new ThreadLocal<>();

    public static void saveUser(User user){
        tl.set(user);
    }

    public static User getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
