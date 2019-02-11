package gavs.first.com.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

import static gavs.first.com.home.Home_Activity.PREFERENCES_FILE_NAME;

public class Courier_Activity extends SessionTimeOutBase {

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";

    TabLayout tabLayout;
    String ip = "";
    EditText etEmpId;
    EditText etEmpName;


    String name = "";
    long empId;

    Button btnSend;
    Button btnReceive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_layout);

        //Defining the widgets


        etEmpId = findViewById(R.id.etempid);
        etEmpName = findViewById(R.id.etempname);
        btnSend = findViewById(R.id.btnsend);
        btnReceive = findViewById(R.id.btnreceive);
        tabLayout = findViewById(R.id.tablayout);


        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);
        ip = myprefs.getString("ip", "");

        //set the edittext with values
        etEmpId.setText(String.valueOf(empId));
        etEmpName.setText(name);

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopDisconnectTimer();

                loadFragment(new Send_Fragment());
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               stopDisconnectTimer();
                loadFragment(new Receive_Fragment());
            }
        });

        /*tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelected(true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition())
                {
                    case 0:
                        loadFragment(new Send_Fragment());
                        break;
                    case 1:
                        loadFragment(new Receive_Fragment());
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

        //For popup window


    }

    public void loadFragment(Fragment fragment) {
        //create Fragment Manager
        FragmentManager fm = getFragmentManager();

        //create Fragment transaction
        FragmentTransaction ft = fm.beginTransaction();

        //replace the layout with fragment

        ft.replace(R.id.framelayoutfrag, fragment);

        //save the changes
        ft.commit();
    }


}
