package com.github.nisrulz.senseysample;

import java.util.Date;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;


@Entity
public class Note {
    @Id
    public long PersonID;

    String name;

    Date date;
    boolean visitor;

    /**
     *
     * @param name
     * @param visitor
     * @param date
     */
    public Note(String name, boolean visitor,  Date date) {
        this.PersonID = PersonID;
        this.name = name;
        this.visitor = visitor;
        this.date = date;
    }

    public Note() { }

    public long getId() {
        return this.PersonID;
    }

    public void setId(long id) {
        this.PersonID = PersonID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    // 'to' is optional if only one relation matches
    @Backlink(to = "notes")
    public ToMany<Face> faces;
}
