package com.n8sqrd.breadcrumbs.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.n8sqrd.breadcrumbs.persistence.BreadcrumbDatabase;

/**
 * Created by ntackett on 1/5/2018.
 */

public class ViewModelFactory implements ViewModelProvider.Factory {
    private final BreadcrumbDatabase mDB;

    public ViewModelFactory(BreadcrumbDatabase db) {
        mDB = db;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PathViewModel.class)) {
            return (T) new PathViewModel(mDB);
        }
        if (modelClass.isAssignableFrom(CrumbViewModel.class)) {
            return (T) new CrumbViewModel(mDB);
        }

        throw new IllegalArgumentException("Unable to locate view model");
    }
}
