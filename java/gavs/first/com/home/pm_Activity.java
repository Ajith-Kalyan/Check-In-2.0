package gavs.first.com.home;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static gavs.first.com.home.Utils.url_pmActivity;


public class pm_Activity extends SessionTimeOutBase {

    //String empID = "", empName = "", empAddr = "", empContNo = "", empDept = "", empReason = "";

    String TAG = "";

    String ip = "";
    long _empId;
    //String _empId="";
    String _empName = "";
    String _email = "";
    String _gender = "";
    long _contNo;
    String _projectName = "";
    String _reportingManager = "";
    String _reportingManagerEmail = "";
    String _address = "";
    String selectedRadioButtonTextTransport = "";
    String selectedRadioButtonTextReason = "";

    int submitcount = 0;

    EditText etEmpName, etEmpId, etEmpDept, etEmpAddr, etEmpContact;

    int selectedRadioButtonID;
    int selectedRadioButtonIDReason;

    Button btSubmit;

    TimePicker tp;

    RadioGroup rbtrans, rbreason;


    RadioButton office;
    RadioButton publicTrans;
    RadioButton critical;
    RadioButton late;
    RadioButton projReq;
    RadioButton others;


    ProgressDialog dialog;


    int hours;
    int minutes;

    private CheckedTextView checkedTextView;

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.declaration_form);


        etEmpName = findViewById(R.id.etempname);
        etEmpId = findViewById(R.id.etempid);
        etEmpAddr = findViewById(R.id.etaddress);
        etEmpContact = findViewById(R.id.etcontactno);
        etEmpDept = findViewById(R.id.etproject);
        btSubmit = findViewById(R.id.bsubmit);
        checkedTextView = findViewById(R.id.checkedTextView);
        tp = findViewById(R.id.simpleTimePicker);
        rbtrans = findViewById(R.id.rmode);
        rbreason = findViewById(R.id.rgReason);


        final RadioButton selectedRadioButton = findViewById(selectedRadioButtonID);
        final RadioButton selectedRadioButtonReason = findViewById(selectedRadioButtonIDReason);


        publicTrans = findViewById(R.id.rpublic);
        office = findViewById(R.id.rcab);
        critical = findViewById(R.id.rdeliverables);
        late = findViewById(R.id.rlateCheck);
        projReq = findViewById(R.id.rprjreq);
        others = findViewById(R.id.rothers);

        //receive field values


        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);


        _empId = myprefs.getLong("empId", 0);
        _empName = myprefs.getString("name", "");
        _email = myprefs.getString("email", "");
        _gender = myprefs.getString("gender", "");
        _contNo = myprefs.getLong("empcontno", 0);
        _projectName = myprefs.getString("projectName", "");
        _reportingManager = myprefs.getString("reportingManager", "");
        _reportingManagerEmail = myprefs.getString("reportingManagerEmail", "");
        _address = myprefs.getString("empaddr", "");
        ip = myprefs.getString("ip", "");


        //setting the field values

        etEmpName.setText(_empName);
        etEmpId.setText(String.valueOf(_empId));
        etEmpAddr.setText(_address);
        etEmpContact.setText(String.valueOf(_contNo));
        etEmpDept.setText(_projectName);


        publicTrans.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (publicTrans.isChecked()) {
                    selectedRadioButtonTextTransport = publicTrans.getText().toString();
                }
            }
        });
        office.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (office.isChecked()) {
                    selectedRadioButtonTextTransport = office.getText().toString();
                    // Toast.makeText(getApplicationContext(),selectedRadioButtonTextTransport,Toast.LENGTH_LONG).show();
                }
            }
        });


        critical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (critical.isChecked()) {
                    selectedRadioButtonTextReason = critical.getText().toString();
                    // Toast.makeText(getApplicationContext(),selectedRadioButtonTextReason,Toast.LENGTH_LONG).show();
                }
            }
        });
        late.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (late.isChecked()) {
                    selectedRadioButtonTextReason = late.getText().toString();
                    // Toast.makeText(getApplicationContext(),selectedRadioButtonTextReason,Toast.LENGTH_LONG).show();
                }
            }
        });
        projReq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (projReq.isChecked()) {
                    selectedRadioButtonTextReason = projReq.getText().toString();
                    // Toast.makeText(getApplicationContext(),selectedRadioButtonTextReason,Toast.LENGTH_LONG).show();
                }
            }
        });
        others.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (others.isChecked()) {
                    selectedRadioButtonTextReason = others.getText().toString();
                    //Toast.makeText(getApplicationContext(),selectedRadioButtonTextReason,Toast.LENGTH_LONG).show();
                }
            }
        });


        tp.is24HourView();
        tp.setHour(20);


        tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hours = tp.getHour();
                minutes = tp.getMinute();
                // Toast.makeText(getApplicationContext(),hours+":"+minutes,Toast.LENGTH_LONG).show();

            }
        });


        checkedTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    checkedTextView.setChecked(false);
                    //Toast.makeText(getApplicationContext(),"unchecked",Toast.LENGTH_SHORT).show();
                    btSubmit.setVisibility(View.GONE);
                } else {
                    checkedTextView.setChecked(true);
                    //Toast.makeText(getApplicationContext(),"checked",Toast.LENGTH_SHORT).show();
                    btSubmit.setVisibility(View.VISIBLE);
                }
            }
        });
        Boolean value = checkedTextView.isChecked();


        //2) create button with onclick listener
        //  2.1)  onclick - new.method.execute
//        if (checkedTextView.isChecked()==true){
//            btSubmit.setVisibility(View.VISIBLE);
//        }else{
//            btSubmit.setVisibility(View.GONE);
//        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validating the fields

                if (selectedRadioButtonTextReason.isEmpty() || selectedRadioButtonTextTransport.isEmpty() || hours == 0 || minutes == 0) {
                    if (selectedRadioButtonTextReason.equals("")) {
                        Toast.makeText(getApplicationContext(), "Choose the reason", Toast.LENGTH_SHORT).show();
                    }
                    if (selectedRadioButtonTextTransport.equals("")) {
                        Toast.makeText(getApplicationContext(), "Choose the Mode of Transport", Toast.LENGTH_SHORT).show();
                    }
                    if (hours == 0 || minutes == 0) {
                        Toast.makeText(getApplicationContext(), "Enter the Time of Reach", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //calling postmethod
                    if (hours < 20 && hours > 12) {
                        Toast.makeText(getApplicationContext(), "Past Time should not be entered", Toast.LENGTH_SHORT).show();
                    } else if (submitcount == 0) {
                        new PostMethodSubmit().execute();
                        submitcount++;
                    }

                }

            }
        });

    }


    //1) declare a async task method (refer signage or home)
    //PostValues(method name)
    class PostMethodSubmit extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(pm_Activity.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                // url = new URL("http://10.0.14.22:8080/gavs/api/print/card");
                //url = new URL("http://192.168.1.81:8081/gavs/api/women/employee");
                url = new URL(url_pmActivity);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());


                JSONObject obj = new JSONObject();


                obj.put("name", _empName);
                obj.put("empId", _empId);
                obj.put("projectName", _projectName);
                obj.put("address", _address);
                obj.put("contactNo", _contNo);
                obj.put("email", _email);
                obj.put("hour", hours);
                obj.put("min", minutes);
                obj.put("mode", selectedRadioButtonTextTransport);
                obj.put("reason", selectedRadioButtonTextReason);

                wr.writeBytes(obj.toString());
                Log.e("JSON Input", obj.toString());
                wr.flush();
                wr.close();


                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                String response = urlConnection.getResponseMessage();


                if (responseCode == HttpURLConnection.HTTP_OK) {
                    dialog.dismiss();


                    return true;
                  /*  Toast.makeText(getApplicationContext(), "Server connected",
                            Toast.LENGTH_LONG).show();*/
//                    server_response = readStream(urlConnection.getInputStream());
                } else {

                    Toast.makeText(getApplicationContext(), "Server not connected", Toast.LENGTH_LONG).show();
                    return false;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("connect", "Error : " + e.getLocalizedMessage(), e);

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error");
                builder.setMessage("An error occured in the URL, kindly report to Admin");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        finish();
                                        Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                        startActivity(i);
                                    }
                                }, 1000);


                            }
                        });

                builder.setCancelable(false);
                builder.show();


                Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return false;

            } catch (IOException e) {
                e.printStackTrace();
                dialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error");
                builder.setMessage("An error occured in the Connectivity, kindly report to Admin");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        finish();
                                        Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                        startActivity(i);
                                    }
                                }, 1000);
                            }
                        });

                builder.setCancelable(false);
                builder.show();


                Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } catch (JSONException e) {
                dialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("Error");
                builder.setMessage("An error occured at the Server, kindly report to Admin");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        finish();
                                        Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                        startActivity(i);
                                    }
                                }, 1000);


                            }
                        });

                builder.setCancelable(false);
                builder.show();


                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);


            Toast.makeText(getApplicationContext(), "Form Submitted",
                    Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(getApplicationContext(), Home_Activity.class);

                    startActivity(i);
                    finish();
                }
            }, 2000);


        }
    }


}
