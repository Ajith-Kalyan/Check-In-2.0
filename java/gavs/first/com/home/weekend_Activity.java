package gavs.first.com.home;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
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
import java.net.ProtocolException;
import java.net.URL;

import static gavs.first.com.home.Home_Activity.PREFERENCES_FILE_NAME;
import static gavs.first.com.home.Utils.url_weekend_attendance;

public class weekend_Activity extends SessionTimeOutBase {

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";

    String ip = "";
    ProgressDialog dialog;
    EditText etEmpId;
    EditText etEmpName;

    public String status;
    int submitcount = 0;

    String name = "", projectName = "", responseStatus="";
    long empId;

    Button btnpunchIn, btnpunchout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekend_layout);

        //Defining the widgets
        etEmpId = findViewById(R.id.etempid);
        etEmpName = findViewById(R.id.etempname);
        btnpunchIn = findViewById(R.id.btnpunchin);
        btnpunchout = findViewById(R.id.btnpunchout);


        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);
        projectName = myprefs.getString("projectName", projectName);
        ip = myprefs.getString("ip", "");


        //set the edittext with values
        etEmpId.setText(String.valueOf(empId));
        etEmpName.setText(name);

        btnpunchIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "checkin";
                if (submitcount == 0) {
                    new PostMethod().execute();
                    submitcount++;
                }
            }
        });

        btnpunchout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "checkout";

                if (submitcount == 0) {
                new PostMethod().execute();
                    submitcount++;
                }

            }
        });


    }

    class PostMethod extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(weekend_Activity.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(url_weekend_attendance);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());


                JSONObject obj = new JSONObject();


                obj.put("empName", name);
                obj.put("empId", empId);
                obj.put("status", status);


                wr.writeBytes(obj.toString());
                Log.e("JSON Input", obj.toString());
                wr.flush();
                wr.close();


                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
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
                    weekend_Activity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(weekend_Activity.this);
                            // Setting Dialog Title
                            alertDialog.setTitle("No Punch-In record found");
                            alertDialog.setCancelable(false);
                            // Setting Dialog Message
                            alertDialog.setMessage("Cannot Punch-out because no previous Punch-In record found!");
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

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    dialog.dismiss();


                    return true;

                } else {


                    return false;
                }
            } catch (ProtocolException e) {
                e.printStackTrace();

                weekend_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(weekend_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        alertDialog.show();
                    }
                });


            } catch (MalformedURLException e) {

                weekend_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(weekend_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("URL has been changed, kindly report to Admin");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        alertDialog.show();
                    }
                });


                //Toast.makeText(getApplicationContext(), "Server not connected", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                weekend_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(weekend_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity(IO), kindly report to Admin");
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        alertDialog.show();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();

                weekend_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(weekend_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        alertDialog.setCancelable(false);
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
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
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (responseStatus.equals("accepted"))
            {
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                        startActivity(i);
                    }
                }, 2000);
            }




        }
    }

}
