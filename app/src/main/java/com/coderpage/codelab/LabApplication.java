package com.coderpage.codelab;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.coderpage.codelab.model.DaoMaster;
import com.coderpage.codelab.model.DaoSession;

/**
 * @author lc. 2017-08-19
 * @since 0.1.0
 */

public class LabApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lab.db");
        SQLiteDatabase db = helper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
