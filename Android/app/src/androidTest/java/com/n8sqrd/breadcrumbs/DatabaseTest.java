package com.n8sqrd.breadcrumbs;


import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;
import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.ui.PathViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ntackett on 1/5/2018.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {


    private BreadcrumbDatabase mDB;
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDB() throws Exception {
        mDB = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),BreadcrumbDatabase.class).allowMainThreadQueries().build();
    }

    @After
    public void closedb() throws  Exception {
        mDB.close();
    }



    @Test
    public void insertAndGetPath() throws Exception {
        Path path = new Path();
        path.setName("Test Path 1");
        path.setStartTime(new Date(System.currentTimeMillis()));

        path.setId(mDB.pathDao().insertPath(path));
        assertNotNull(path.getId());
        path.setId(null);
        path.setId(mDB.pathDao().insertPath(path));





        mDB.pathDao().retrievePathById(path.getId()).test().assertValue( p -> {
            return p.getName().equals("Test Path 1") && p.getId()==path.getId();
        });

    }
//    @Test
//    public void updateAndGetPath() throws Exception {
//        Path path = new Path();
//        path.setName("Original path");
//        path.setStartTime(new Date(System.currentTimeMillis()));
//
//        path.setId(mDB.pathDao().insertPath(path));
//        assertNotNull(path.getId());
//
//        path.setName("Updated");
//
//        mDB.pathDao().updatePath(path);
//
//        mDB.pathDao().retrievePathById(path.getId()).test().assertValue( p -> {
//            return p.getName().equals("Updated") && p.getId()==path.getId();
//        });
//    }
//
//    @Test
//    public void retrieveAllPaths() throws Exception {
//        //Make sure there is at least one
//        Path path = new Path();
//        path.setName("Original path");
//        path.setStartTime(new Date(System.currentTimeMillis()));
//
//        path.setId(mDB.pathDao().insertPath(path));
//        assertNotNull(path.getId());
//
//        mDB.pathDao().retrieveAllPaths().test().assertValue(paths ->{
//            return paths!=null && paths.size()>0;
//        });
////        mDB.pathDao().retrieveAllPaths().test().subscribe(paths -> {
////            for (Path ppp : paths) {
////                Log.d("dbg",ppp.name);
////            }
////        });
//    }
//    @Test
//    public void deletePath() throws Exception {
//        //Make sure there is at least one
//        Path path = new Path();
//        path.setName("Original path");
//        path.setStartTime(new Date(System.currentTimeMillis()));
//
//        path.setId(mDB.pathDao().insertPath(path));
//        assertNotNull(path.getId());
//
//        mDB.pathDao().deletePath(path.getId());
//
//        mDB.pathDao().retrievePathById(path.getId()).test().assertNoValues();
//    }
//
//    @Test
//    public void retrieveCurrentPath() throws Exception {
//        //Make sure there is at least one
//        Path path = new Path();
//        path.setName("Original path");
//        path.setStartTime(new Date(System.currentTimeMillis()));
//
//        path.setId(mDB.pathDao().insertPath(path));
//        assertNotNull(path.getId());
//
//        mDB.pathDao().retrieveCurrentPath().test().assertValue(p -> {
//           return p.getId()==path.getId();
//        });
//    }
}
