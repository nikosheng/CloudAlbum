package hku.cs.cloudalbum;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerActivity extends Activity implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    public static final String TAG = "VideoPlayer";
    private VideoView mVideoView;
    private Uri mUri;
    private int mPositionWhenPaused = -1;
    private MediaController mMediaController;
    private Button prevButton;
    private Button nextButton;
    private HashMap<String, Object> previtem;
    private HashMap<String, Object> nextitem;
    private int cur_position;
    private List<Map<String, Object>> itemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        //Set the screen to landscape.
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        prevButton = (Button)findViewById(R.id.prev);
        nextButton = (Button)findViewById(R.id.next);

        mVideoView = (VideoView) findViewById(R.id.video_view);
        mMediaController = new MediaController(this);

        //Video file
        itemList = (List<Map<String, Object>>)getIntent().getSerializableExtra("videoitems");
        cur_position = getIntent().getIntExtra("position", 0);
        setPlayList(cur_position, itemList);

        String murl = (String)itemList.get(cur_position).get("videoUrl");
        mUri = Uri.parse(murl);

        playVideo(mUri);

        prevButton.setOnClickListener(new View.OnClickListener() {
            String prevurl = (String) previtem.get("videoUrl");
            Uri prevuri = Uri.parse(prevurl);
            @Override
            public void onClick(View view) {
                mVideoView.setVideoURI(prevuri);
                setPlayList(getPrevPosition(itemList.size(), cur_position), itemList);
                mVideoView.start();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            String nexturl = (String) previtem.get("videoUrl");
            Uri nexturi = Uri.parse(nexturl);
            @Override
            public void onClick(View view) {
                mVideoView.setVideoURI(nexturi);
                setPlayList(getNextPosition(itemList.size(), cur_position), itemList);
                mVideoView.start();
            }
        });
    }

    private void setPlayList(int position, List<Map<String, Object>> itemList) {
        cur_position = position;

        int prevPosition = getPrevPosition(itemList.size(), position);
        int nextPosition = getNextPosition(itemList.size(), position);

        previtem = (HashMap<String, Object>) itemList.get(prevPosition);
        nextitem = (HashMap<String, Object>) itemList.get(nextPosition);

        String prevname = (String) previtem.get("videoName");
        String nextname = (String) nextitem.get("videoName");

        prevButton.setText(prevname);
        nextButton.setText(nextname);
    }

    private int getPrevPosition(int length, int position) {
        int ret;
        if (position == 0) {
            ret = length - 1;
        } else {
            ret = position - 1;
        }
        return ret;
    }

    private int getNextPosition(int length, int position) {
        int ret;
        if (position >= length - 1) {
            ret = 0;
        } else {
            ret = position + 1;
        }
        return ret;
    }

    public void playVideo(Uri _uri) {
        final Uri uri = _uri;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mVideoView.setMediaController(mMediaController);
                mVideoView.setVideoURI(uri);
                mVideoView.start();
            }
        }).start();
    }

    public void onStart() {

        super.onStart();
    }

    public void onPause() {
        // Stop video when the activity is pause.
        mPositionWhenPaused = mVideoView.getCurrentPosition();
        mVideoView.stopPlayback();
        Log.d(TAG, "OnStop: mPositionWhenPaused = " + mPositionWhenPaused);
        Log.d(TAG, "OnStop: getDuration  = " + mVideoView.getDuration());

        super.onPause();
    }

    public void onResume() {
        // Resume video player
        if (mPositionWhenPaused >= 0) {
            mVideoView.seekTo(mPositionWhenPaused);
            mPositionWhenPaused = -1;
        }

        super.onResume();
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        this.finish();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }


}
