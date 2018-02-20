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
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by ntackett on 1/4/2018.
 */
@Dao
@TypeConverters({DateConverter.class})
public interface PathDao {
    @Query("select * from Path where id=:id")
    Single<Path> retrievePathById(Long id);

    @Query("select * from Path order by start_time, name")
    Flowable<List<Path>> retrieveAllPaths();

    @Query("select * from Path where end_time is null order by start_time desc limit 1")
    Maybe<Path> retrieveCurrentPath();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertPath(Path path);

    @Query("delete from Path where id=:id")
    void deletePath(Long id);

    @Update
    void updatePath(Path... path);
}
