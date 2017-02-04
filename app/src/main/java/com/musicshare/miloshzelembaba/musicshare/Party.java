package com.musicshare.miloshzelembaba.musicshare;

/**
 * Created by miloshzelembaba on 2017-01-18.
 */

public class Party {
    private String partyOwner;
    private String partyName;
    private LoginWindowInfo loginWindowInfo;
    static Party instance = null;

    private Party(){}

    public static Party getInstance(){
        if (instance == null){
            instance = new Party();
            return instance;
        }
        return instance;
    }

    public void setInfo(LoginWindowInfo lwi){
        loginWindowInfo = lwi;
        setPartyInfo();
    }

    public void setPartyName(String name){
        partyName = name;
    }

    private void setPartyInfo(){
        partyOwner = loginWindowInfo.getUserName();
    }
}
