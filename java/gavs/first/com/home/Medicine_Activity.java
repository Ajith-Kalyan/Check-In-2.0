package gavs.first.com.home;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static gavs.first.com.home.Home_Activity.PREFERENCES_FILE_NAME;
import static gavs.first.com.home.Utils.url_authorize;
import static gavs.first.com.home.Utils.url_medicine;

public class Medicine_Activity extends SessionTimeOutBase {

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";

    EditText etEmpId;
    EditText etEmpName;
    EditText etMedName;
    EditText etReason;
    EditText etQuantity;

    String ip = "";

    TextView submittv;
    Button submit;
    int submitcount = 0;
    ProgressDialog dialog;

    String name = "", medicineName = "", reason = "", qty = "";
    String adminProjectName = "", adminEmpId = "", adminName = "";
    String tagid = "";
    long empId;
    int quantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medicine_layout);

        //Defining the widgets

        etEmpId = findViewById(R.id.etempid);
        etEmpName = findViewById(R.id.etempname);
        etMedName = findViewById(R.id.etmedicinename);
        etReason = findViewById(R.id.etreason);
        etQuantity = findViewById(R.id.etquantity);

        submit = findViewById(R.id.buttonsubmit);

        SharedPreferences myprefs = getSharedPreferences(PREFERENCES_FILE_NAME, 0);

        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);
        ip = myprefs.getString("ip", "");


        etEmpId.setText(String.valueOf(empId));
        etEmpName.setText(name);

        //OnSubmit
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                reason = etReason.getText().toString();
                qty = etQuantity.getText().toString();
                //quantity = Integer.parseInt(etQuantity.getText().toString());
                medicineName = etMedName.getText().toString();


                if (medicineName.equals("") || reason.equals("") || qty.equals("")) {
                    if (medicineName.equals("")) {
                        etMedName.setError("Enter the Medicine name");
                    }
                    if (reason.equals("")) {
                        etReason.setError("Enter the reason");
                    }
                    if (etQuantity.getText().toString().equals("")) {
                        etQuantity.setError("Input the quantity");
                    }
                } else {
                    if (submitcount == 0) {

                        Medicine_Activity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                AlertDialog.Builder alert = new AlertDialog.Builder(Medicine_Activity.this);
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
                                        if(submitcount == 0) {
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
            dialog = new ProgressDialog(Medicine_Activity.this);
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

                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
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

                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
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

                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
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
                    new PostMethod(adminName, adminEmpId).execute();
                    submitcount++;
                }
            } else {
                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
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

    class PostMethod extends AsyncTask<String, String, Boolean> {

        String admin_name = "", admin_emp_id = "";

        public PostMethod (String admin_name, String admin_emp_id) {
            this.admin_name = admin_name;
            this.admin_emp_id = admin_emp_id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Medicine_Activity.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                //url = new URL("http://192.168.1.81:8081/gavs/api/Medicine/data");
                url = new URL(url_medicine);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());


                JSONObject obj = new JSONObject();


                obj.put("name", name);
                obj.put("empId", empId);
                obj.put("medicineName", medicineName);
                obj.put("reason", reason);
                obj.put("quantity", qty);
                obj.put("adminName",admin_name);
                obj.put("adminEmpId",admin_emp_id);

                wr.writeBytes(obj.toString());
                Log.e("JSON Input", obj.toString());
                wr.flush();
                wr.close();


                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                String response = urlConnection.getResponseMessage();


                if (responseCode == HttpURLConnection.HTTP_OK) {
                    dialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_LONG).show();

                    return true;
                  /*  Toast.makeText(getApplicationContext(), "Server connected",
                            Toast.LENGTH_LONG).show();*/
//                    server_response = readStream(urlConnection.getInputStream());
                } else {

                    Toast.makeText(getApplicationContext(), "Server not connected", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (ProtocolException e) {
                e.printStackTrace();

                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


            } catch (MalformedURLException e) {
                e.printStackTrace();


                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
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


            } catch (IOException e) {
                e.printStackTrace();

                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
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


            } catch (JSONException e) {
                e.printStackTrace();

                Medicine_Activity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Medicine_Activity.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
                        alertDialog.setCancelable(false);
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_LONG).show();
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
