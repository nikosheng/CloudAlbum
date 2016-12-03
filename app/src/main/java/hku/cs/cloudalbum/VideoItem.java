package hku.cs.cloudalbum;

/**
 * Created by Niko Feng on 11/27/2016.
 */

public class VideoItem {
    private int video_id;
    private String video_name;
    private String uploadTimeStamp;
    private String video_url;

    public VideoItem(int video_id, String video_name, String uploadTimeStamp, String video_url) {
        this.video_id = video_id;
        this.video_name = video_name;
        this.uploadTimeStamp = uploadTimeStamp;
        this.video_url = video_url;
    }

    public int getVideo_id() {
        return video_id;
    }

    public void setVideo_id(int video_id) {
        this.video_id = video_id;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getUploadTimeStamp() {
        return uploadTimeStamp;
    }

    public void setUploadTimeStamp(String uploadTimeStamp) {
        this.uploadTimeStamp = uploadTimeStamp;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}
