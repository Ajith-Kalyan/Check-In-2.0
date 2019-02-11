package gavs.first.com.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static gavs.first.com.home.Home_Activity.PREFERENCES_FILE_NAME;
import static gavs.first.com.home.Utils.url_authorize;
import static gavs.first.com.home.Utils.url_umbrella;

public class Umbrella_Activity extends SessionTimeOutBase {

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";

    EditText etEmpId;
    EditText etEmpName;
    EditText etAuthorize;
    Button datePickerFrom;
    Button datePickerTo;
    String ip = "";
    Activity activity;

    String adminProjectName = "", adminEmpId = "", adminName = "",responseStatus="";

    String tagid = "";

    int submitcount = 0;

    RadioButton rbreturn, rbreceive;

    String name = "", status = "";
    long empId;
    Button submit;
    DatePickerDialog datePickerDialog;

    ProgressDialog dialog;
    String formattedDate = "";

    // int fday,fmonth,fyear,day,month, year,hour,minute;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umbrella_layout);
        //Defining the widgets

        etEmpId = findViewById(R.id.etempid);
        etEmpName = findViewById(R.id.etempname);
        datePickerFrom = findViewById(R.id.dpfrom);
        datePickerTo = findViewById(R.id.dpto);
        submit = findViewById(R.id.btnsubmit);
        rbreceive = findViewById(R.id.rbreceive);
        rbreturn = findViewById(R.id.rbreturn);
        activity = this.activity;
        etAuthorize = findViewById(R.id.etauthorize);


        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);
        ip = myprefs.getString("ip", "");

        //set the edittext with values
        etEmpId.setText(String.valueOf(empId));
        etEmpName.setText(name);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rbreceive.isChecked()) {
                    status = rbreceive.getText().toString();
                } else if (rbreturn.isChecked()) {
                    status = rbreturn.getText().toString();
                }
                if (!status.isEmpty()) {
                    if (submitcount == 0) {

                        Umbrella_Activity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                AlertDialog.Builder alert = new AlertDialog.Builder(Umbrella_Activity.this);
                                View dialogLayout = inflater.inflate(R.layout.authorize_popup, null);
                                final EditText etAuthorize = dialogLayout.findViewById(R.id.etauthorize);
                                alertDialog.setView(dialogLayout);
                                alertDialog.setCancelable(false);
                                AlertDialog dialog = alert.create();
                                dialog.show();

                                etAuthorize.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        tagid = etAuthorize.getText().toString();
                                        if (tagid.length() == 10) {
                                            new PostMethodAuthorize(tagid).execute();
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (submitcount == 0) {
                                            finish();
                                            Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                            startActivity(i);
                                        }
                                    }
                                });
                                alertDialog.show();
                            }
                        });

                        submitcount++;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Choose an option", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    public class PostMethodAuthorize extends AsyncTask<String, String, Boolean> {

        String finalTag = "";

        public PostMethodAuthorize(String tag) {
            finalTag = tag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Umbrella_Activity.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {

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
                String responseStr = response.body().string();

                JSONObject responseObj = new JSONObject(responseStr);

                adminProjectName = responseObj.getString("projectName");
                adminEmpId = responseObj.getString("empId");
                adminName = responseObj.getString("name");


                if (response.code() == HttpURLConnection.HTTP_OK) {

                    return true;

                } else {

                    //Toast.makeText(getContext(), "Server not connected", Toast.LENGTH_LONG).show();
                    return false;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("connect", "Error : " + e.getLocalizedMessage(), e);

                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("URL has been changed, kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        alertDialog.show();
                    }
                });

                return false;

            } catch (IOException e) {
                e.printStackTrace();

                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("An error occured in the Connectivity, kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        alertDialog.show();
                    }
                });

                return false;
            } catch (JSONException e) {
                e.printStackTrace();

                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        alertDialog.show();
                    }
                });


            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            int submitcount = 0;
            if (adminProjectName.equals("Admin")) {
                if (submitcount == 0) {
                    new PostMethodSubmit(adminName, adminEmpId).execute();
                    submitcount++;
                }
            } else {
                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        alertDialog.setTitle("Error!");
                        alertDialog.setMessage("Invalid Authentication");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        alertDialog.show();
                    }
                });
            }
        }
    }


    //Asynctask
    public class PostMethodSubmit extends AsyncTask<String, String, Boolean> {

        String admin_name = "", admin_emp_id = "";

        public PostMethodSubmit(String admin_name, String adminId) {
            this.admin_name = admin_name;
            this.admin_emp_id = adminId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Umbrella_Activity.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {


                url = new URL(url_umbrella);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());


                JSONObject obj = new JSONObject();

                obj.put("name", name);
                obj.put("empId", empId);
                obj.put("status", status);
                obj.put("adminEmpId",admin_emp_id);
                obj.put("adminName",admin_name);

                wr.writeBytes(obj.toString());
                Log.e("JSON Input", obj.toString());
                wr.flush();
                wr.close();


                urlConnection.connect();

                final int responseCode = urlConnection.getResponseCode();
                String response = urlConnection.getResponseMessage();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line = reader.readLine();

                JSONObject jsonObject = new JSONObject(line);
                JSONArray jsonArray = (JSONArray) jsonObject.get("result");

                for (int i = 0;i<jsonArray.length();i++) {
                    JSONObject j = jsonArray.getJSONObject(i);
                    responseStatus = j.getString("response");
                }

                if (responseStatus.equals("rejected"))
                {
                    Umbrella_Activity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                            // Setting Dialog Title
                            alertDialog.setTitle("No Record found for receiving an Umbrella");
                            alertDialog.setCancelable(false);
                            // Setting Dialog Message
                            alertDialog.setMessage("Kindly let the person who received the umbrella to return it");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                    startActivity(i);

                                }
                            });
                            alertDialog.show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                            startActivity(i);
                        }
                    }, 2000);
                }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    dialog.dismiss();
                    return true;
                } else {
                    Umbrella_Activity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                            alertDialog.setTitle("Error!");
                            alertDialog.setMessage("An" + responseCode + "error occured, kindly report to Admin");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                    startActivity(i);
                                }
                            });
                            alertDialog.show();
                        }
                    });
                    return false;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("connect", "Error : " + e.getLocalizedMessage(), e);

                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity(IO), kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        alertDialog.show();
                    }
                });


                //Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return false;

            } catch (IOException e) {
                e.printStackTrace();
                dialog.dismiss();

                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity(IO), kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        alertDialog.show();
                    }
                });


                //Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } catch (JSONException e) {
                dialog.dismiss();
                Umbrella_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Umbrella_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity(IO), kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        alertDialog.show();
                    }
                });

                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
           // Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();

/*            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                    startActivity(i);
                }
            }, 2000);*/
        }
    }


}
