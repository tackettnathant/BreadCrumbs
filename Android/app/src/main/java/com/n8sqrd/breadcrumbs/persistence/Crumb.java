package com.n8sqrd.breadcrumbs.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.n8sqrd.breadcrumbs.utils.DateConverter;

import java.util.Date;

/**
 * Created by ntackett on 1/4/2018.
 */
@Entity(foreignKeys = @ForeignKey(entity = Path.class,parentColumns = "id",childColumns = "path_id",onDelete = ForeignKey.CASCADE), indices = {@Index(value = {"path_id"})})
public class Crumb implements Parcelable{
    public Crumb(){}

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "path_id")
    private Long pathId;

    @TypeConverters({DateConverter.class})
    private Date timestamp;

    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "uri")
    private String URI;
    @ColumnInfo(name = "notes")
    private String notes;
    @ColumnInfo(name = "location")
    private String location;
    @ColumnInfo(name = "latitude")
    private Double latitude;
    @ColumnInfo(name = "longitude")
    private Double longitude;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(pathId);
        Long dateTime = timestamp!=null?timestamp.getTime():null;
        parcel.writeLong(dateTime);
        parcel.writeString(type);
        parcel.writeString(URI);
        parcel.writeString(notes);
        parcel.writeString(location);
        if (latitude!=null) parcel.writeDouble(latitude);
        if (longitude!=null) parcel.writeDouble(longitude);
    }
    public static final Parcelable.Creator<Crumb> CREATOR = new Parcelable.Creator<Crumb>(){

        @Override
        public Crumb createFromParcel(Parcel parcel) {
            return new Crumb(parcel);
        }

        @Override
        public Crumb[] newArray(int size) {
            return new Crumb[size];
        }
    };

    public Crumb(Parcel in) {
        this.id = in.readLong();
        this.pathId = in.readLong();
        Long dateTime = in.readLong();
        this.timestamp = dateTime!=null?new Date(in.readLong()):null;
        this.type = in.readString();
        this.URI = in.readString();
        this.notes = in.readString();
        this.location = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPathId() {
        return pathId;
    }

    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }





}
