package com.n8sqrd.breadcrumbs.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by ntackett on 2/19/2018.
 */

@Dao
public interface PromptDAO {
    @Query("select * from Prompt order by RANDOM()")
    Flowable<List<Prompt>> retrieveAllPrompts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertPrompt(Prompt prompt);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPrompts(Prompt... prompts );

    @Update
    void updatePrompt(Prompt promp);

    @Query("delete from Prompt where id=:id")
    void deletePrompt(Long id);
}
