package hku.cs.cloudalbum;
import android.widget.TextView;

public class ListViewItem {
    public TextView videoId;
    public TextView videoName;
    public TextView videoTime;
    public TextView videoUrl;

    public ListViewItem(){}



    public ListViewItem(TextView videoId, TextView videoName, TextView videoTime, TextView videoUrl) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoTime = videoTime;
        this.videoUrl = videoUrl;
    }

    public TextView getVideoId() {
        return videoId;
    }

    public void setVideoId(TextView videoId) {
        this.videoId = videoId;
    }

    public TextView getVideoName() {
        return videoName;
    }

    public void setVideoName(TextView videoName) {
        this.videoName = videoName;
    }

    public TextView getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(TextView videoTime) {
        this.videoTime = videoTime;
    }

    public TextView getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(TextView videoUrl) {
        this.videoUrl = videoUrl;
    }
}
