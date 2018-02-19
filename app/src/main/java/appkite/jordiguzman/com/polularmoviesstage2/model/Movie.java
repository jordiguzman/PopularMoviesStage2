package appkite.jordiguzman.com.polularmoviesstage2.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    private String mId;
    private String mTitle;
    private String mMoviePoster;
    private String mPlot;
    private String mRating;
    private String mReleaseDate;


    public Movie(){
    }


    private Movie(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mMoviePoster = in.readString();
        mPlot = in.readString();
        mRating = in.readString();
        mReleaseDate = in.readString();

    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmMoviePoster() {
        return mMoviePoster;
    }

    public void setmMoviePoster(String mMoviePoster) {
        this.mMoviePoster = mMoviePoster;
    }

    public String getmPlot() {
        return mPlot;
    }

    public void setmPlot(String mPlot) {
        this.mPlot = mPlot;
    }

    public String getmRating() {
        return mRating;
    }

    public void setmRating(String mRating) {
        this.mRating = mRating;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public void setmReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mMoviePoster);
        dest.writeString(mPlot);
        dest.writeString(mRating);
        dest.writeString(mReleaseDate);

    }
}
