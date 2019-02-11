package gavs.first.com.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Female_activity extends SessionTimeOutBase {

    private static final String TAG = "";
    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";


    TextView tv;

    ImageView ivPmDeclaration;
    ImageView ivLostnFound;
    ImageView ivUmbrella;
    ImageView ivMedicine;
    ImageView ivWeekend;
    ImageView ivCourier;
    ImageView ivBarCode;
    CardView cvpmdec;

    String name="";
    String gender="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.female_layout);

        //Defining widgets

        tv=findViewById(R.id.tvwelcome);
        ivPmDeclaration = findViewById(R.id.imgv8pm);
        ivLostnFound = findViewById(R.id.imgvlostandfound);
        ivUmbrella = findViewById(R.id.imgvumbrella);
        ivWeekend = findViewById(R.id.imgvhelmet);
        ivCourier =  findViewById(R.id.imgvcourier);
        ivMedicine = findViewById(R.id.imgvmedicine);
        ivBarCode = findViewById(R.id.ivbarcode);
        cvpmdec = findViewById(R.id.cardpm);


        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        name = myprefs.getString("name","");
        gender = myprefs.getString("empgender","");

        if(gender.equals("Male"))
        {
            cvpmdec.setElevation(0);
           // ivPmDeclaration.setEnabled(false);
        }else if(gender.equals("Female"))
        {
            cvpmdec.setEnabled(true);
        }

        if(name.length()<= 20){
           // Toast.makeText(getApplicationContext(),"less than 20",Toast.LENGTH_SHORT).show();
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,33);
            tv.setText("Welcome "+name);
        }else if(name.length()>20){
            //Toast.makeText(getApplicationContext(),"greater than 20",Toast.LENGTH_SHORT).show();
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,25);
            //tv.setTextSize(45);
            tv.setText("Welcome "+name);
        }



        //*8pm declaration intent
        ivPmDeclaration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.equals("Male")){
                    Toast.makeText(getApplicationContext(),"You don't have to fill this form",Toast.LENGTH_LONG).show();
                }else{

                    Intent i = new Intent(getApplicationContext(), pm_Activity.class);
                    startActivity(i);

                }

            }
        });

        //lost and found intent declaration

        ivLostnFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Lost_Activity.class);
                startActivity(i);

            }
        });

        //Umbrella intent declaration
        ivUmbrella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Umbrella_Activity.class);
                startActivity(i);

            }
        });

        //Medicine intent declaration

        ivMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Medicine_Activity.class);
                startActivity(i);

            }
        });

        //weekend intent declaration

        ivWeekend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String weekday = "";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.US);
                Calendar c = Calendar.getInstance();
                weekday = simpleDateFormat.format(c.getTime());

                if(weekday.equals("Saturday") || weekday.equals("Sunday")) {
                    Intent i = new Intent(getApplicationContext(), weekend_Activity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Today is a weekday",Toast.LENGTH_LONG).show();
                }
            }
        });

        //Courier intent declaration

        ivCourier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),Courier_Activity.class);
                startActivity(i);

            }
        });

        ivBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ScannerActivity.class);
                startActivity(i);

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),Home_Activity.class);
        startActivity(i);
        finish();
    }
}




