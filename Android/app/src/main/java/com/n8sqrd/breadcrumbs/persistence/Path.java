package com.n8sqrd.breadcrumbs.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.n8sqrd.breadcrumbs.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ntackett on 1/4/2018.
 */
@Entity
public class Path implements Parcelable{

    public Path(){

    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "name")
    private String name;

    @TypeConverters({DateConverter.class})
    @ColumnInfo(name = "start_time")
    private Date startTime;

    @TypeConverters({DateConverter.class})
    @ColumnInfo(name= "end_time")
    private Date endTime;

    @ColumnInfo(name = "current_location")
    private String currentLocation;

    @ColumnInfo(name = "image_uri")
    private String imageURI;

    @ColumnInfo(name = "latitude")
    private Double latitude;
    @ColumnInfo(name = "longitude")
    private Double longitude;
    @Ignore
    private List<Crumb> crumbs;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        Long dt = startTime!=null?startTime.getTime():null;
        parcel.writeLong(dt);
        dt = endTime!=null?endTime.getTime():null;
        parcel.writeString(currentLocation);
        parcel.writeString(imageURI);
        if (latitude!=null) {
            parcel.writeDouble(latitude);
        }
        if (longitude!=null) parcel.writeDouble(longitude);
        if (crumbs!=null) parcel.writeTypedList(crumbs);
    }

    public static Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>() {

        @Override
        public Path createFromParcel(Parcel parcel) {
            return new Path(parcel);

        }

        @Override
        public Path[] newArray(int size) {
            return new Path[size];
        }
    };
    public Path(Parcel parcel) {
        this.id=parcel.readLong();
        this.name=parcel.readString();
        Long dt = parcel.readLong();
        this.startTime = dt!=null?new Date(dt):null;
        dt = parcel.readLong();
        this.endTime = dt!=null?new Date(dt):null;
        this.currentLocation = parcel.readString();
        this.imageURI = parcel.readString();
        this.latitude = parcel.readDouble();
        this.longitude = parcel.readDouble();
        crumbs = new ArrayList<Crumb>();
        parcel.readTypedList(crumbs,Crumb.CREATOR);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
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

    public List<Crumb> getCrumbs() {
        return crumbs;
    }

    public void setCrumbs(List<Crumb> crumbs) {
        this.crumbs = crumbs;
    }
}
