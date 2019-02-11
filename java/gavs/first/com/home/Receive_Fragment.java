package gavs.first.com.home;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static gavs.first.com.home.Utils.url_courier_receive;

public class Receive_Fragment extends SessionTimeOutBaseFragment {

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";
    EditText etItem, etFrom, etInvoiceDetails;


    public static int responseCode;
    RadioButton personal, official;
    Button btnSubmit;

    String ip = "", TAG = "";

    String _vendroname = "", _from = "", _type = "", _invoice = "";
    View view;

    int submitcount = 0;
    ProgressDialog dialog;
    String name = "";
    long empId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.receivefragment_layout, container, false);


        SharedPreferences myprefs = getActivity().getSharedPreferences(PREFERENCES_FILE_NAME, 0);

        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);
        ip = myprefs.getString("ip", "");

        //Declare all the widgets used
        etItem = view.findViewById(R.id.etitem);
        etFrom = view.findViewById(R.id.etfrom);
        btnSubmit = view.findViewById(R.id.btnsendsumbit);
        personal = view.findViewById(R.id.rbpersonal);
        official = view.findViewById(R.id.rbofficial);
        etInvoiceDetails = view.findViewById(R.id.etinvoice);


        //get value of radiobutton
        personal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (personal.isChecked()) {
                    _type = personal.getText().toString();
                }
            }
        });
        official.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (official.isChecked()) {
                    _type = official.getText().toString();
                }
            }
        });
        etFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetDisconnectTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etInvoiceDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetDisconnectTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                resetDisconnectTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //set button onclick listner

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //store values to string
                _vendroname = etItem.getText().toString();
                _from = etFrom.getText().toString();
                _invoice = etInvoiceDetails.getText().toString();


                if (_vendroname.equals("") || _from.equals("") || _invoice.equals("")) {
                    if (_vendroname.equals("")) {
                        etItem.setError("Enter vendor name");
                    }
                    if (_invoice.equals("")) {
                        etInvoiceDetails.setError("Enter Invoice Details");
                    }
                    if (_from.equals("")) {
                        etFrom.setError("Enter from detail");
                    }
                } else {
                    if (submitcount == 0) {
                        new postMethod().execute();
                        submitcount++;
                    }
                }


/*                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent i = new Intent(getContext(),Home_Activity.class);
                        startActivity(i);
                    }
                }, 2000);*/
            }
        });

        return view;
    }


    class postMethod extends AsyncTask<String, String, Boolean> {

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(url_courier_receive);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());


                JSONObject obj = new JSONObject();

                obj.put("name", name);
                obj.put("empId", empId);
                obj.put("vendorName", _vendroname);
                obj.put("invoiceDetails", _invoice);
                //  obj.put("type", _type);


                wr.writeBytes(obj.toString());
                Log.e("JSON Input", obj.toString());
                wr.flush();
                wr.close();


                urlConnection.connect();

                responseCode = urlConnection.getResponseCode();
                String response = urlConnection.getResponseMessage();


                if (responseCode == HttpURLConnection.HTTP_OK) {
                    dialog.dismiss();


                    return true;

                } else {

                    return false;
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e("connect", "Error : " + e.getLocalizedMessage(), e);

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("URL has been changed, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


                // Toast.makeText(getActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return false;

            } catch (IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity(IO), kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });

                //Toast.makeText(getActivity(), "Server not connected", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                dialog.dismiss();
                //Toast.makeText(getActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } catch (JSONException e) {


                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });

                dialog.dismiss();
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            super.onPostExecute(aBoolean);
            if (responseCode == 200) {
                Toast.makeText(getContext(), "Submitted", Toast.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        getActivity().getFragmentManager().popBackStack();
                        Intent i = new Intent(getActivity(), Home_Activity.class);
                        startActivity(i);
                    }
                }, 2000);
            } else {
                Log.d(TAG, "onPostExecute: " + responseCode);
                //todo add alert dialog

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });
                // Toast.makeText(getContext(),"Unsuccessful",Toast.LENGTH_LONG).show();
                dialog.dismiss();

            }

        }
    }
}
