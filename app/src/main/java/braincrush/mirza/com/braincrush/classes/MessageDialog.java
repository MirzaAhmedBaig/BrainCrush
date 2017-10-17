package braincrush.mirza.com.braincrush.classes;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import braincrush.mirza.com.braincrush.R;
import braincrush.mirza.com.braincrush.constants.GameConstants;

/**
 * Created by MIRZA on 17/10/17.
 */

public class MessageDialog extends DialogFragment implements android.view.View.OnClickListener {

    private static final String TITLE = "title";
    private static final String MSG = "msg";
    private static final String TYPE = "type";
    private Button ok_button, yes_button, no_button;
    private TextView msg, head;
    private View view;

    public static MessageDialog newInstance(String title, String msg, int alertType) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MSG, msg);
        args.putInt(TYPE, alertType);
        MessageDialog fragment = new MessageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    //        requestWindowFeature(Window.FEATURE_NO_TITLE);
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        head = view.findViewById(R.id.title);
        msg = view.findViewById(R.id.message);
        this.view = view.findViewById(R.id.type2);
        ok_button = view.findViewById(R.id.btn_ok);
        yes_button = view.findViewById(R.id.btn_yes);
        no_button = view.findViewById(R.id.btn_no);

        ok_button.setOnClickListener(this);
        yes_button.setOnClickListener(this);
        no_button.setOnClickListener(this);
        createAlert();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private void createAlert() {
        head.setText(getArguments().getString(TITLE));
        msg.setText(getArguments().getString(MSG));
        if (getArguments().getInt(TYPE) == GameConstants.WARNING_ALERT) {
            ok_button.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == yes_button) {
            getActivity().finish();
        } else {
            this.dismiss();
        }
    }
}