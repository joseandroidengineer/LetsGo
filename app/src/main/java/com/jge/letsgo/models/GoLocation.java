package com.jge.letsgo.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "goLocation")
public class GoLocation implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("latitude")
    public float latitude;
    @SerializedName("longitude")
    public float longitude;
    @SerializedName("description")
    public String description;

    public GoLocation(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeFloat(latitude);
        parcel.writeFloat(longitude);
        parcel.writeString(description);

    }

    public static final Parcelable.Creator<GoLocation> CREATOR = new Parcelable.Creator<GoLocation>() {
        public GoLocation createFromParcel(Parcel in){
            return new GoLocation(in);
        }

        public GoLocation[] newArray(int size){
            return new GoLocation[size];
        }
    };

    private GoLocation(Parcel in){
        id = in.readInt();
        name = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
        description = in.readString();

    }
}
