package com.github.nisrulz.senseysample;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class Face {
    @Id
    public long imageID;

    byte[] RGB_data;
    byte[] infrared_data;
    byte[] feature;

    String name;

    // 'to' is optional if only one relation matches
    public ToOne<Note> notes;

    /**
     *
     * @param name
     * @param RGB_data
     * @param infrared_data
     * @param feature
     */
    public Face(String name, byte[] RGB_data, byte[] infrared_data, byte[] feature)
    {
        this.name = name;
        this.feature = feature;
        this.infrared_data = infrared_data;
        this.RGB_data = RGB_data;
    }



    public Face(){ }
}
