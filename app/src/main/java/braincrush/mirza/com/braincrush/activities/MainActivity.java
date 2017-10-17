package braincrush.mirza.com.braincrush.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import braincrush.mirza.com.braincrush.R;
import braincrush.mirza.com.braincrush.classes.GameInfo;
import braincrush.mirza.com.braincrush.classes.MessageDialog;
import braincrush.mirza.com.braincrush.constants.GameConstants;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button singlePlayer, multiPlayer, settings;
    private ImageButton share, info, like;
    private DisplayMetrics displayMetrics;
    private GameInfo gameInfo;
    private MessageDialog messageDialog;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initViews();
        initDisplayMatrix();
        setUpListener();
    }

    private void initViews() {
        singlePlayer = findViewById(R.id.single);
        multiPlayer = findViewById(R.id.multi);
        settings = findViewById(R.id.settings);
        like = findViewById(R.id.like_btn);
        share = findViewById(R.id.share_btn);
        info = findViewById(R.id.info_btn);
    }

    private void setUpListener() {
        singlePlayer.setOnClickListener(this);
        multiPlayer.setOnClickListener(this);
        settings.setOnClickListener(this);
        like.setOnClickListener(this);
        share.setOnClickListener(this);
        info.setOnClickListener(this);
    }

    private void initDisplayMatrix() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    public void singlePlay() {
        Intent intent = new Intent(this, SinglePlayerActivity.class);
        startActivity(intent);

    }

    public void multiPlay() {
        messageDialog = MessageDialog.newInstance("App in progress!", "Thanks for testing my app. I will soo add this functionality.", GameConstants.SIMPLE_ALERT);
        messageDialog.show(getFragmentManager(), "multiPlayerMsg");
    }

    public void settings() {
        messageDialog = MessageDialog.newInstance("App in progress!", "Thanks for testing my app. I will soo add this functionality.", GameConstants.SIMPLE_ALERT);
        messageDialog.show(getFragmentManager(), "settingMsg");
    }

    public void like() {
        messageDialog = MessageDialog.newInstance("App in progress!", "Thanks for testing my app. I will soo add this functionality.", GameConstants.SIMPLE_ALERT);
        messageDialog.show(getFragmentManager(), "likeMsg");
    }

    public void info() {
        gameInfo = new GameInfo();
        gameInfo.show(getFragmentManager(), "gameInfoMsg");
    }

    public void share() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "I love The Brain Twist App...! \nThis is the best game to boost your brain ability.\nDownload it from here\nhttps://drive.google.com/open?id=0By5B0_-J_O5tWTY3NkFKVkx5ZHM";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "The Brain Gain App");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single:
                singlePlay();
                break;
            case R.id.multi:
                multiPlay();
                break;
            case R.id.settings:
                settings();
                break;
            case R.id.like_btn:
                like();
                break;
            case R.id.share_btn:
                share();
                break;
            case R.id.info_btn:
                info();
                break;
        }
    }
}