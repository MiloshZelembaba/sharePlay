package com.miloshzelembaba.play.Utils;

import com.miloshzelembaba.play.Models.User;

/**
 * Created by miloshzelembaba on 2018-04-03.
 */

public class ApplicationUtil {
    private static ApplicationUtil instance;
    private static User user;

    private ApplicationUtil(){}

    public static ApplicationUtil getInstance() {
        if (instance == null) {
            instance = new ApplicationUtil();
        }

        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        user = u;
    }


}
