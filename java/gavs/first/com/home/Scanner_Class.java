package gavs.first.com.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static gavs.first.com.home.Utils.host_ip;
import static gavs.first.com.home.Utils.url_barcode;
import static gavs.first.com.home.Utils.url_umbrella;

public class Scanner_Class extends SessionTimeOutBase implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    ProgressDialog dialog;
    String assetId = "";

    String empid;
    String empdept = "", empname = "", img = "";
    String assetdata="";
    String PREFERENCES_Image="Checkin";


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // Log.v(TAG, rawResult.getContents()); // Prints scan results
        // Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        if (rawResult != null) {

            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
            assetId = rawResult.getContents();
            new PostAssetId(assetId).execute();

            //Toast.makeText(getApplicationContext(), rawResult.getContents(), Toast.LENGTH_LONG).show();
        }
        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
        //mScannerView.stopCamera();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public  class PostAssetId extends AsyncTask<String, String, Boolean> {

        String barcode = "";


        public PostAssetId(String assetId) {
            barcode = assetId;
            //barcode="200411CHNLTP0056";
            //barcode="200411CHNLTP0063";
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(Scanner_Class.this);
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            try {

                //GET Method
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url_barcode+barcode)
                        .get()
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "484c9fd5-56e7-3e93-28ed-d2a9986ec7e0")
                        .build();

                Response response = client.newCall(request).execute();
                String responseStr = response.body().string();

                final int responseCode = response.code();


                if (!responseStr.isEmpty())
                {


                    JSONArray jsonArray = new JSONArray(responseStr);

                    for (int i = 0; i < jsonArray.length(); i++){

                        JSONObject jsonresponse = jsonArray.getJSONObject(i);

                        empdept = jsonresponse.getString("Dept");
                        empname = jsonresponse.getString("EmpName");
                        empid = jsonresponse.getString("EmpID");
                        img = jsonresponse.getString("SharedPath");

                    }


                    return true;

                } else {

                    Scanner_Class.this.runOnUiThread(new Runnable() {
                        public void run() {
                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Scanner_Class.this);
                            // Setting Dialog Title
                            alertDialog.setTitle("Error!");
                            // Setting Dialog Message
                            alertDialog.setMessage("An "+responseCode+" error occured , kindly report to Admin");
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
                    return false;
                }
            } catch (ProtocolException e) {
                e.printStackTrace();

                Scanner_Class.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Scanner_Class.this);
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
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


            } catch (MalformedURLException e) {
                e.printStackTrace();


                Scanner_Class.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Scanner_Class.this);
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


            } catch (IOException e) {
                e.printStackTrace();

                Scanner_Class.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Scanner_Class.this);
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

                Scanner_Class.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Scanner_Class.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
//                // Setting Icon to Dialog
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
            if (!empname.isEmpty() && !empid.isEmpty() && !empdept.isEmpty() && !img.isEmpty()) {
                Intent i = new Intent(getApplicationContext(), ScannerActivity.class);
                i.putExtra("empname", empname);
                i.putExtra("empid", empid);
                i.putExtra("empdept", empdept);
//                i.putExtra("img", img);
                setResult(1, i);

                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(PREFERENCES_Image,0);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("img",img);
                editor.apply();

                finish();
            }
            else {
                Scanner_Class.this.runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(Scanner_Class.this);
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("Your Barcode isn't registered in our Database, kindly report to Admin");
//                // Setting Icon to Dialog
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),Female_activity.class);
        startActivity(i);
        finish();
    }
}

