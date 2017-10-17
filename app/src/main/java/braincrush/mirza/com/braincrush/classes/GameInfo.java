package braincrush.mirza.com.braincrush.classes;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import braincrush.mirza.com.braincrush.R;

/**
 * Created by MIRZA on 17/10/17.
 */

public class GameInfo extends DialogFragment implements View.OnClickListener {

    private TextView link;
    private ImageButton facebook, twitter, insta;
    private Intent browserIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_info_layout, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        link = view.findViewById(R.id.link);
        facebook = view.findViewById(R.id.facebook);
        twitter = view.findViewById(R.id.twitter);
        insta = view.findViewById(R.id.insta);
        createMessage();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void createMessage() {
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        insta.setOnClickListener(this);
        link.setClickable(true);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        link.setLinkTextColor(Color.rgb(237, 138, 25));
        String text = "Website: <a href='http://www.mirzaahmed.tk'>www.mirzaahmed.tk</a><br><br>Credit :<br>Icons made by Gregor Cresnar from <a href='https://www.flaticon.com/'>www.flaticon.com</a> is licensed byCC 3.0 BY";
        link.setText(Html.fromHtml(text));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.facebook:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mirzahmed65"));
                startActivity(browserIntent);
                break;
            case R.id.twitter:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/mirzaahmedbaig8"));
                startActivity(browserIntent);
                break;
            case R.id.insta:
                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/immirzaahmed/"));
                startActivity(browserIntent);
                break;
        }
    }
}
