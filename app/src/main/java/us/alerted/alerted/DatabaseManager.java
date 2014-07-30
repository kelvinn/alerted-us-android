package us.alerted.alerted;

/**
 * Created by kelvinn on 30/07/2014.
 */
import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import us.alerted.alerted.Alert;

public class DatabaseManager {

    static private DatabaseManager instance;

    static public void init(Context ctx) {
        if (null==instance) {
            instance = new DatabaseManager(ctx);
        }
    }

    static public DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseHelper helper;
    private DatabaseManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    private DatabaseHelper getHelper() {
        return helper;
    }

    public List<Alert> getAllAlerts() {
        List<Alert> wishLists = null;
        try {
            wishLists = getHelper().getAlertDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return wishLists;
    }
}
