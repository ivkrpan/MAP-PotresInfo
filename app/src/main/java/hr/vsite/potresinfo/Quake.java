package hr.vsite.potresinfo;
import java.io.Serializable;

public class Quake implements Serializable {

    private String mID;
    private String mLocation;
    private double mMagnitude;
    private long mDate;
    private String mURL;
    private String mOffset;

    public Quake (String id, String loc, String offset, double mag, long dat, String url) {
        mID = id;
        mLocation = loc;
        mMagnitude = mag;
        mDate = dat;
        mURL = url;
        mOffset = offset;
    }

    public String getmLocation() {
        return mLocation;
    }
    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmOffset() {
        return mOffset;
    }
    public void setmOffset(String mOffset) {
        this.mOffset = mOffset;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }
    public void setmMagnitude(double mMagnitude) {
        this.mMagnitude = mMagnitude;
    }

    public long getmDate() {
        return mDate;
    }
    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public String getmURL() {
        return mURL;
    }
    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

    public String getmID() {
        return mID;
    }
    public void setmID(String mID) {
        this.mID = mID;
    }

}
