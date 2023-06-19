package com.ultimabyte.bpoultry.utils;

import android.util.Log;


/**
 * Log helper class to log message to logcat only when app is running in debug mode.
 * Author: qijaz221@gmail.com
 */

public class Logger {

    /**
     * Logs the given message to {@link Log#d(String, String)} only when running in debug mode.
     *
     * @param tag Tag to use.
     * @param msg message to log.
     */
    public static void d(String tag, String msg) {
        //check if tag or message is null.
        if (tag == null || msg == null) {
            //early return.
            return;
        }

        //check if app is running in debug mode.
        //log the message.
        Log.d(tag, msg);
    }


    /**
     * Logs the given message to {@link Log#w(String, String)} only when running in debug mode.
     *
     * @param tag Tag to use.
     * @param msg message to log.
     */
    public static void w(String tag, String msg) {
        //check if tag or message is null.
        if (tag == null || msg == null) {
            //early return.
            return;
        }

        //check if app is running in debug mode.
        //log the message.
        Log.w(tag, msg);
    }
}
