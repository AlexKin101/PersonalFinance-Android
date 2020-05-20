package com.example.personalfinancedemo;

import android.os.Parcel;
import android.os.Parcelable;

public class Record implements Parcelable {
    private int recordId = 0;
    private String recordDate = "";
    private String recordType = "";
    private Double recordAmount = 0.00;
    private String recordExplain = "";
    private String recordOwner = "";

    public int getRecordIcon() {
        return recordIcon;
    }

    public void setRecordIcon(int recordIcon) {
        this.recordIcon = recordIcon;
    }

    private int recordIcon=0;

    //getter&setter
    public int getRecordId() { return recordId; }

    public void setRecordId(int recordId) { this.recordId = recordId; }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public Double getRecordAmount() {
        return recordAmount;
    }

    public void setRecordAmount(Double recordAmount) {
        this.recordAmount = recordAmount;
    }

    public String getRecordExplain() {
        return recordExplain;
    }

    public void setRecordExplain(String recordExplain) {
        this.recordExplain = recordExplain;
    }

    public String getRecordOwner() { return recordOwner; }

    public void setRecordOwner(String recordOwner) { this.recordOwner = recordOwner; }


    public Record() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.recordId);
        dest.writeString(this.recordDate);
        dest.writeString(this.recordType);
        dest.writeValue(this.recordAmount);
        dest.writeString(this.recordExplain);
        dest.writeString(this.recordOwner);
        dest.writeInt(this.recordIcon);
    }

    protected Record(Parcel in) {
        this.recordId = in.readInt();
        this.recordDate = in.readString();
        this.recordType = in.readString();
        this.recordAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.recordExplain = in.readString();
        this.recordOwner = in.readString();
        this.recordIcon = in.readInt();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel source) {
            return new Record(source);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };
}