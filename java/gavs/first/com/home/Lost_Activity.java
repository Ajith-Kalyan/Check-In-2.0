package gavs.first.com.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class Lost_Activity extends SessionTimeOutBase {


    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";


    EditText etEmpId;
    EditText etEmpName;

    String name = "";
    long empId;

    Button btnReport, btnClaim;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lostnfound_layout);

        //Defining the widgets


        etEmpId = findViewById(R.id.etempid);
        etEmpName = findViewById(R.id.etempname);
        btnReport = findViewById(R.id.btnreport);
        btnClaim = findViewById(R.id.btnclaim);


        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);

        //set the edittext with values
        etEmpId.setText(String.valueOf(empId));
        etEmpName.setText(name);


        //set button onclick Listener

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SessionTimeOutBase().stopDisconnectTimer();
                loadFragment(new ReportFragment());
            }
        });

        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDisconnectTimer();
                loadFragment(new ClaimFragment());
            }
        });


    }


    public void loadFragment(Fragment fragment) {
        //create Fragment manager
        FragmentManager fm = getFragmentManager();

        //create fragment transaction
        FragmentTransaction ft = fm.beginTransaction();

        //replace the layout with fragment
        ft.replace(R.id.framelayoutfrag, fragment);

        ft.commit();//commit the transaction
    }

}
