package com.miloshzelembaba.play.Error;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by miloshzelembaba on 2018-04-28.
 */

public class ErrorService {

    public enum ErrorSeverity {
        LOW,
        MEDIUM,
        HIGH
    }

    public static void showErrorMessage(Context context, String error, ErrorSeverity severity) {
        Toast t;
        if (severity == ErrorSeverity.HIGH) {
            t = Toast.makeText(context, error, Toast.LENGTH_LONG);
        } else {
            t = Toast.makeText(context, error, Toast.LENGTH_SHORT);
        }

        t.show();
    }

}
