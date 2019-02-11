package gavs.first.com.home;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {


    EditText empName;

    public BlankFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        String strtext = getArguments().getString("empFname");

        View view = inflater.inflate(R.layout.declaration_form, container, false);

        return view;



    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final CheckedTextView checkedTextView = view.findViewById(R.id.checkedTextView);
        if (checkedTextView != null) {
            checkedTextView.setChecked(false);
            checkedTextView.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedTextView.setChecked(!checkedTextView.isChecked());
                    checkedTextView.setCheckMarkDrawable(checkedTextView.isChecked() ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);

                }
            });
        }



    }
}
