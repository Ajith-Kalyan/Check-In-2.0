package gavs.first.com.home;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class AuthorizationPopupDialog extends DialogFragment {

    Activity activity;
    ProgressDialog progressDialog;
    EditText etTagId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_layout, container, false);

        etTagId = v.findViewById(R.id.etauthorize);
        etTagId.requestFocus();
        etTagId.setShowSoftInputOnFocus(true);
        etTagId.setClickable(true);
        etTagId.setFocusableInTouchMode(true);

        return null;
    }
    }

