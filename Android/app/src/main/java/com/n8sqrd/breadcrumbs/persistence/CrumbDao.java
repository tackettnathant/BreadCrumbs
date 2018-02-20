package com.n8sqrd.breadcrumbs.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.n8sqrd.breadcrumbs.utils.DateConverter;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Created by ntackett on 1/5/2018.
 */

@Dao
@TypeConverters({DateConverter.class})
public interface CrumbDao {
    @Query("select * from Crumb where id=:id")
    Single<Crumb> retrieveCrumbById(Long id);

    @Query("select * from Crumb where path_id=:id order by timestamp asc")
    Single<List<Crumb>> retrieveCrumbsForPath(Long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCrumb(Crumb crumb);

    @Query("delete from Crumb where id=:id")
    void deleteCrumb(Long id);

    @Query("delete from Crumb where path_id=:id")
    void deleteCrumbsForPath(Long id);

    @Update
    void updateCrumb(Crumb... crumb);
}
