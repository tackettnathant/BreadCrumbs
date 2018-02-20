package com.n8sqrd.breadcrumbs.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.n8sqrd.breadcrumbs.utils.Constants;

/**
 * Created by ntackett on 2/19/2018.
 */
@Entity
public class Prompt {

    public Prompt() {

    }
    public Prompt(String text,Integer context) {
        this.text=text;
        this.context=context;
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "context")
    private Integer context;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getContext() {
        return context;
    }

    public void setContext(Integer context) {
        this.context = context;
    }

    public static Prompt[] populatePrompts() {
        return new Prompt[]{
            new Prompt("Take a selfie.", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Take a picture of someone who didn't come with you.", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Take a picture of the person to your left.", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Take a picture of the person to your right.", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Take a picture of the loudest person there.", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Have someone else take a picture of you.", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Mean mug selfie", Constants.PROMPT_CONTEXT_ALL),
                new Prompt("Take a picture of the best looking person you can see.", Constants.PROMPT_CONTEXT_ALL)
        };
    }
}
