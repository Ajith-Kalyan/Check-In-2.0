package gavs.first.com.home;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import gavs.first.com.home.Home_Activity;
import gavs.first.com.home.R;
import gavs.first.com.home.Scanner_Class;

public class ScannerActivity extends SessionTimeOutBase {
    Button btnScan;
    EditText etEmpName;
    EditText etEmpId;
    EditText etEmpDept;
    String PREFERENCES_Image= "Checkin";


    ImageView ivPhoto;

    String finalImg="";

    String _empId="", _empName="", _empDept ="", _image="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(getApplicationContext(),Scanner_Class.class);
                startActivityForResult(i,1);
            }
        },10);

        btnScan = findViewById(R.id.scanBarcodeButton);
        etEmpName= findViewById(R.id.etempname);
        etEmpId = findViewById(R.id.etempid);
        etEmpDept = findViewById(R.id.etempdept);

        ivPhoto = findViewById(R.id.ivphoto);



        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            etEmpName.setText(data.getStringExtra("empname"));
            etEmpDept.setText(data.getStringExtra("empdept"));
            etEmpId.setText(data.getStringExtra("empid"));
            //_image = data.getStringExtra("img");
            SharedPreferences myprefs = getSharedPreferences(PREFERENCES_Image, 0);
            _image = myprefs.getString("img","");

            String[] arrStr = _image.split(",");
            finalImg = arrStr[1];

            byte[] imageBytes = Base64.decode(finalImg, Base64.DEFAULT);
            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            ivPhoto.setBackgroundColor(000000);
            ivPhoto.setImageBitmap(decodedImage);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    Intent i = new Intent(getApplicationContext(),Home_Activity.class);
                    startActivity(i);
                }
            },3000);
        }
    }
}

