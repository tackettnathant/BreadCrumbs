package com.n8sqrd.breadcrumbs.ui;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;
import com.n8sqrd.breadcrumbs.persistence.Crumb;
import com.n8sqrd.breadcrumbs.persistence.Path;
import com.n8sqrd.breadcrumbs.utils.Constants;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Created by ntackett on 1/30/2018.
 */

public class CrumbViewModel extends ViewModel {
    private final BreadcrumbDatabase mDB;




    private Crumb mWorkingCrumb;
    public CrumbViewModel(BreadcrumbDatabase db) { mDB = db;}
    public Completable insertUpdateCrumb(Crumb crumb) {
        Log.i(Constants.TAG,"Insert/Update crumb. ID: " + crumb.getId() + ". Path ID: " + crumb.getPathId());
        return Completable.fromAction(() -> {
            crumb.setId(mDB.crumbDao().insertCrumb(crumb)); //onconflict will handle update

        });
    }

    public Crumb getWorkingCrumb() {
        return mWorkingCrumb;
    }

    public void setWorkingCrumb(Crumb crumb) {
        this.mWorkingCrumb = crumb;
    }

}
