package com.n8sqrd.breadcrumbs.ui;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;
import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.persistence.Prompt;
import com.n8sqrd.breadcrumbs.utils.Constants;
import com.n8sqrd.breadcrumbs.utils.UtilityMethods;

import java.util.List;
import java.util.Observable;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by ntackett on 1/5/2018.
 */

public class PathViewModel extends ViewModel {
    private final BreadcrumbDatabase mDB;
    private Path currentPath;
    private Path deletedPath;
    public List<Path> mAllPaths;
    public List<Prompt> mAllPrompts;

    public PathViewModel(BreadcrumbDatabase db) {
        mDB = db;
    }

    public Flowable<List<Prompt>> getAllPrompts() {
        return  mDB.promptDAO().retrieveAllPrompts().map(prompts -> {
            mAllPrompts=prompts;
            return prompts;
        });
    }

    public Flowable<List<Path>> getAllPaths() {
        if (mAllPaths!=null) {
            //return Flowable.just(mAllPaths);
        }
        return mDB.pathDao().retrieveAllPaths().map(paths -> {
            Log.i(Constants.TAG,"pathDAO.retrieveAllPaths fired");
            mAllPaths=paths;
            return paths;
        });
    }

    public Maybe<Path> retrieveActivePath(){
        if (currentPath !=null) {
            Maybe.just(currentPath);
        }
        return  mDB.pathDao().retrieveCurrentPath().map(path -> {
            Log.i(Constants.TAG,"pathDAO.retrieveCurrentPath fired");
            currentPath =path;
            return path;
        });


    }

    public Single<Path> retrieveCrumbsForPath(Path path){
        return mDB.crumbDao().retrieveCrumbsForPath(path.getId())
                .map(crumbs -> {
                    Log.i(Constants.TAG,"crumbDAO.retrieveCrumbsForPath fired. Path ID: "+path.getId());
                    path.setCrumbs(crumbs);
                    return path;
                });

    }
    public Single<Path> retrievePath(Long id) {
        return Single.zip(mDB.pathDao().retrievePathById(id),mDB.crumbDao().retrieveCrumbsForPath(id),
                (path, crumbs) -> {
                    Log.i(Constants.TAG, "retrievePath. Retrieved path: " + path.getId());
                    path.setCrumbs(crumbs);
                    currentPath=path;
                    return path;
                });
    }

    public Completable insertUpdatePath(Path path) {
        return Completable.fromAction(() -> {
            UtilityMethods.logStart("PathViewModel.insertUpdatePath");
            boolean newPath = path.getId()==null;
            path.setId(mDB.pathDao().insertPath(path)); //onconflict will handle update
            currentPath =null;
            if (newPath && path.getCrumbs()!=null) {
                for (Crumb crumb : path.getCrumbs()){
                    crumb.setPathId(path.getId());
                }
            }
            Log.i(Constants.TAG, "Path updated/inserted. ID: " + path.getId());
            UtilityMethods.logEnd("PathViewModel.insertUpdatePath");

        });
    }
    public Completable insertUpdateCrumb(Crumb crumb) {
        Log.i(Constants.TAG,"Inserting crumb");
        return Completable.fromAction(() -> {
            crumb.setId(mDB.crumbDao().insertCrumb(crumb)); //onconflict will handle update
            Log.i(Constants.TAG,"Crumb insert/update complete. ID: " + crumb.getId());
        });
    }
    public Completable deletePath(long pathid) {
        return Completable.fromAction(()->{
            mDB.pathDao().deletePath(pathid);
                }

        );
    }
    public void setCurrentPath(Path path) {
        this.currentPath =path;
    }
    public void setDeletedPath(Path path) {
        this.deletedPath =path;
    }
    public Path getCurrentPath() {
        return this.currentPath;
    }
    public Path getDeletedPath() {
        return this.deletedPath;
    }
}
