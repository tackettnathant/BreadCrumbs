package com.n8sqrd.breadcrumbs.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.n8sqrd.breadcrumbs.utils.Constants;

import java.util.Date;
import java.util.concurrent.Executors;

/**
 * Created by ntackett on 1/5/2018.
 */
@Database(entities = {Path.class,Crumb.class,Prompt.class}, version=1)
public abstract class BreadcrumbDatabase extends RoomDatabase {
    private static volatile BreadcrumbDatabase DB;

    public abstract PathDao pathDao();
    public abstract CrumbDao crumbDao();
    public abstract PromptDAO promptDAO();

    public static BreadcrumbDatabase getInstance(Context context) {
        if (DB == null) {
            synchronized (BreadcrumbDatabase.class) {
                if (DB == null) {
                    DB = Room.databaseBuilder(context, BreadcrumbDatabase.class, Constants.DB_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            getInstance(context).promptDAO().insertPrompts(Prompt.populatePrompts());
                                        }
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return DB;
    }


}
