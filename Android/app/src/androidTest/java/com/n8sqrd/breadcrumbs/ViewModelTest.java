package com.n8sqrd.breadcrumbs;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;
import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.ui.PathViewModel;
import com.n8sqrd.breadcrumbs.ui.ViewModelFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by ntackett on 1/6/2018.
 */
@RunWith(AndroidJUnit4.class)
public class ViewModelTest {
    private BreadcrumbDatabase mDB;
    private Path retrieved;
    private List<Path> mPaths;
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

//    @Test
//    public void testViewModelFactory() throws Exception {
//        ViewModelFactory factory = new ViewModelFactory(mDB);
//        assertNotNull(factory);
//
//        PathViewModel model = factory.create(PathViewModel.class);
//        assertNotNull(model);
//    }
    @Test
    public void insertAndGetTheWholeDamnedThing() {
        PathViewModel vm = new PathViewModel(mDB);
        Path path = new Path();
        path.setName("Test Path 1");
        path.setStartTime(new Date(System.currentTimeMillis()));
        path.setId(mDB.pathDao().insertPath(path));

        vm.getAllPaths().subscribe(paths -> {
            mPaths = paths;
        });
        Crumb crumb = new Crumb();
        crumb.setPathId(path.getId());
        crumb.setLocation("Home");
        crumb.setTimestamp(new Date(System.currentTimeMillis()));
        crumb.setId(mDB.crumbDao().insertCrumb(crumb));


        vm.retrievePath(path.getId()).test().assertValue(p->{
           return p.getCrumbs().size()==1;
        });

    }

//    @Test
//    public void testInsertGetPath() throws Exception {
//        ViewModelFactory factory = new ViewModelFactory(mDB);
//        assertNotNull(factory);
//
//        PathViewModel model = factory.create(PathViewModel.class);
//        assertNotNull(model);
//
//        Path path = new Path();
//        path.name = "original";
//        path.startTime=new Date(System.currentTimeMillis());
//        path.endTime=new Date(System.currentTimeMillis());
//
//
//        model.getAllPaths().subscribeOn(Schedulers.newThread()).subscribe(paths -> {
//            for (Path p : paths) {
//                System.out.println("Path id: " + p.id);
//            }
//        });
//        model.insertUpdateCrumb(path).test().assertComplete();
//
//        path.endTime=null;
//        model.insertUpdateCrumb(path).test().assertComplete();
//        path.endTime=new Date(System.currentTimeMillis());
//        model.insertUpdateCrumb(path).test().assertComplete();
//        assertNotNull(path.id);
//
//        model.retrievePath(path.id).subscribe(p -> {
//            assertEquals(p.id,path.id);
//        });
//    }
//    @Test
//    public void testUpdateGetPath() throws Exception {
//        ViewModelFactory factory = new ViewModelFactory(mDB);
//        assertNotNull(factory);
//
//        PathViewModel model = factory.create(PathViewModel.class);
//        assertNotNull(model);
//
//        Path path = new Path();
//        path.name = "original";
//        path.startTime=new Date(System.currentTimeMillis());
//
//        model.insertUpdateCrumb(path).test().assertComplete();
//        assertNotNull(path.id);
//
//        path.name = "new";
//        model.insertUpdateCrumb(path).test().assertComplete();
//
//        model.retrievePath(path.id).subscribe(p -> {
//            assertEquals(p.name,path.name);
//        });
//    }
//    @Test
//    public void testRetrieveAllPaths() throws Exception {
//        ViewModelFactory factory = new ViewModelFactory(mDB);
//        assertNotNull(factory);
//
//        PathViewModel model = factory.create(PathViewModel.class);
//        assertNotNull(model);
//
//        Path path = new Path();
//        path.name = "original";
//        path.startTime=new Date(System.currentTimeMillis());
//
//        model.insertUpdateCrumb(path).test().assertComplete();
//        assertNotNull(path.id);
//        Path path2 = new Path();
//        path2.name = "original";
//        path2.startTime=new Date(System.currentTimeMillis());
//
//        model.insertUpdateCrumb(path2).test().assertComplete();
//        assertNotNull(path2.id);
//        model.getAllPaths().test().assertValue(p -> {
//            return p!=null && p.size()>0;
//        });
//        model.getAllPaths().test().assertValue(p -> {
//            return p!=null && p.size()>0;
//        });
//    }
//
//    @Test
//    public void testGetCurrent() throws Exception {
//        ViewModelFactory factory = new ViewModelFactory(mDB);
//        assertNotNull(factory);
//
//        PathViewModel model = factory.create(PathViewModel.class);
//        assertNotNull(model);
//
//        Path path = new Path();
//        path.name = "original";
//        path.startTime=new Date(System.currentTimeMillis());
//
//        model.insertUpdateCrumb(path).test().assertComplete();
//
//        assertNotNull(path.id);
//
//        model.retrieveCurrentPath().test().assertValue(p -> {
//            return p.id==path.id;
//        });
//        model.retrieveCurrentPath().test().assertValue(p -> {
//            return p.id==path.id;
//        });
//    }
//private  RoomDatabase.Callback rdc = new RoomDatabase.Callback(){
//    @Override
//    public void onCreate(@NonNull SupportSQLiteDatabase db) {
//        super.onCreate(db);
//        Path path = new Path();
//        path.name = "First path";
//        path.startTime = new Date(System.currentTimeMillis());
//
//        mDB.pathDao().insertPath(path);
//    }
//};
}
