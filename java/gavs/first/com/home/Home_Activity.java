package gavs.first.com.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.RequestQueue;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static gavs.first.com.home.Utils.url_authorize;

public class Home_Activity extends Activity {

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";
    ProgressDialog dialog;

    String ip = "192.168.1.217";
    EditText et;
    EditText etEmpName;
    TextView tv;
    TextView tvMarquee;
    ImageView refresh;

    String tag = "";

    String empName = "";
    //String empId="";
    long empId;
    String empEmail = "";
    String empGender = "";
    String empProjectName = "";
    String reportingManager = "";
    String reportingManagerEmail = "";
    String empAddr = "";
    long empContNo;
    String TAG = "";
    String packageName = "";


    PostMethod postMethod;

    Button btnNavNonEmp;
    Button btnNavEmp;

    ViewFlipper viewFlipper;

    public int hour;




/*
    //for image slider
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;
*/
/*

    RequestQueue rq;
    List<SliderUtils> sliderImg;
    ViewPagerAdapter viewPagerAdapter;

    String request_url = "";
*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*
         */
/**
 * For Image Slider
 *//*


        rq = CustomVolleyRequest.getInstance(this).getRequestQueue();

        sliderImg = new ArrayList<>();

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);

        sendRequest();


*/

        et = findViewById(R.id.et1);
        tv = findViewById(R.id.tv1);
        btnNavEmp = findViewById(R.id.btnEmp);
        btnNavNonEmp = findViewById(R.id.btnNonEmp);
        tvMarquee = findViewById(R.id.MarqueeText);
        etEmpName = findViewById(R.id.etempname);
        viewFlipper = (ViewFlipper) findViewById(R.id.flipperid);
        refresh = findViewById(R.id.refresh);

        tvMarquee.setSelected(true);
        viewFlipper.startFlipping();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
                et.requestFocus();
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tag = et.getText().toString();
                tv.setText(tag);
                if (et.getText().toString().length() == 10) {

                    postMethod = new PostMethod(tag);
                    //Toast.makeText(getApplicationContext(),"http://"+ip+":8081/gavs/api/employee/authorize",Toast.LENGTH_LONG).show();
                    postMethod.execute();

                    et.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + tag);


            }
        });
        /*
        //For Image Slider

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

*/


        final Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);


        //Toast.makeText(getApplicationContext(),strhr,Toast.LENGTH_SHORT).show();
        btnNavNonEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageName = "gavs.first.com.signage";
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                    finish();
                    btnNavEmp.setVisibility(View.GONE);
                    btnNavNonEmp.setVisibility(View.GONE);
                }
            }
        });

        btnNavEmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(tag, "onClick: " + empGender + "hour:" + hour);
                //Toast.makeText(getApplicationContext(), "onClick: "+empGender+"hour:"+hour,Toast.LENGTH_LONG).show();

                // if (empGender.equals("Female") && (hour >=9 || hour <=06)) {


                    Intent intent;
                    intent = new Intent(getApplicationContext(), Female_activity.class);
                    startActivity(intent);

                    btnNavEmp.setVisibility(View.GONE);
                    btnNavNonEmp.setVisibility(View.GONE);


            }
        });

        final CheckedTextView checkedTextView = findViewById(R.id.checkedTextView);
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

    public class PostMethod extends AsyncTask<String, String, Boolean> {

        String finalTag = "";

        public PostMethod(String tag) {
            finalTag = tag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            dialog = new ProgressDialog(Home_Activity.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
//            URL url;
//            HttpURLConnection urlConnection = null;


            try {


                Log.d(tag, "doInBackground: " + finalTag);
                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "\n{\n\t\"finalTag\":\"" + finalTag + "\"\n}");
                Request request = new Request.Builder()
                        .url(url_authorize)
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "00d5f1bd-0c76-49a0-cf57-4764526dbb48")
                        .build();

                Response response = client.newCall(request).execute();


                if (response.code() == HttpURLConnection.HTTP_OK) {


                    String responseStr = response.body().string();

                    JSONObject responseObj = new JSONObject(responseStr);


                    //empId = Integer.parseInt((String) responseObj.get("empId"));

                    empName = responseObj.getString("name");
                    empId = responseObj.getLong("empId");
                    empEmail = responseObj.getString("email");
                    empProjectName = responseObj.getString("projectName");
                    reportingManager = responseObj.getString("reportingManager");
                    reportingManagerEmail = responseObj.getString("reportingManagerEmail");
                    empGender = responseObj.getString("gender");
                    empAddr = responseObj.getString("address");
                    empContNo = responseObj.getLong("contactNo");

                    return true;
                    //server_response = readStream(urlConnection.getInputStream());
                } else if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {

                    dialog.dismiss();
                    Home_Activity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_Activity.this);
                            // Setting Dialog Title
                            alertDialog.setTitle("Error!");
                            // Setting Dialog Message
                            alertDialog.setMessage("Your Id isn't registered, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                            // Setting OK Button
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    et.setText("");
                                    et.requestFocus();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.show();
                        }
                    });
                    return false;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("connect", "Error : " + e.getLocalizedMessage(), e);
                dialog.dismiss();

                Home_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("URL has been changed, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et.setText("");
                                et.requestFocus();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });
                return false;

            } catch (IOException e) {
                e.printStackTrace();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Home_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the connectivity(IO)");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et.setText("");
                                et.requestFocus();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


//                Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
                Home_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et.setText("");
                                et.requestFocus();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });

            } catch (RuntimeException e) {
                e.printStackTrace();
                dialog.dismiss();
                Home_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et.setText("");
                                et.requestFocus();
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });
            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFERENCES_FILE_NAME, 0);
            SharedPreferences.Editor editor = sharedPref.edit();

            //editor.putString("empId", String.valueOf(empId));

            editor.putString("ip", ip);
            editor.putLong("empId", empId);
            editor.putString("name", empName);
            editor.putString("email", empEmail);
            editor.putString("empgender", empGender);
            editor.putLong("empcontno", empContNo);
            editor.putString("projectName", empProjectName);
            editor.putString("reportingManager", reportingManager);
            editor.putString("reportingManagerEmail", reportingManagerEmail);
            editor.putString("address", empAddr);

            editor.apply();
            // editor.commit();

            if (empProjectName.equals("Admin")) {
                btnNavNonEmp.setVisibility(View.VISIBLE);
                btnNavEmp.setVisibility(View.VISIBLE);
                dialog.dismiss();
                Log.d("hello", "dept " + empProjectName);
            } else if (empGender.equals("Female") && (hour >= 9 || hour <= 06)) {
                Log.d("hello", "hour : " + hour);
                Intent i = new Intent(getApplicationContext(), Female_activity.class);
                startActivity(i);
                finish();
                dialog.dismiss();
            } else if (empGender.equals("Male")) {
                Intent i = new Intent(getApplicationContext(), Female_activity.class);
                startActivity(i);
                finish();
                dialog.dismiss();
                //Toast.makeText(getApplicationContext(),"Have a Happy Day "+empName,Toast.LENGTH_LONG).show();
            }
        }
    }


/*    //Receiving data for image slider

    public void sendRequest(){
//JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, request_url, null, new Response.Listener<JSONArray>()


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.class,request_url,null,new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {



                for(int i = 0; i < response.length(); i++){

                    SliderUtils sliderUtils = new SliderUtils();

                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        sliderUtils.setSliderImageUrl(jsonObject.getString("image_url"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sliderImg.add(sliderUtils);

                }

                viewPagerAdapter = new ViewPagerAdapter(sliderImg, Home_Activity.this);

                viewPager.setAdapter(viewPagerAdapter);

                dotscount = viewPagerAdapter.getCount();
                dots = new ImageView[dotscount];

                for(int i = 0; i < dotscount; i++){

                    dots[i] = new ImageView(Home_Activity.this);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    params.setMargins(8, 0, 8, 0);

                    sliderDotspanel.addView(dots[i], params);

                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));


            }
        },new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

            CustomVolleyRequest.getInstance(this).addToRequestQueue(jsonArrayRequest);


}*/

    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed: working");
    }
}
