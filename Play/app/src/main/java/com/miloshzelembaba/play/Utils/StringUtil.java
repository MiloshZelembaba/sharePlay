package com.miloshzelembaba.play.Utils;

/**
 * Created by miloshzelembaba on 2018-03-25.
 * Provides some random functions for modifying strings
 */

public class StringUtil {

    public static String padZeros(String partyId){
        while (partyId.length() < 6){
            partyId = "0" + partyId;
        }

        return partyId;
    }
}
