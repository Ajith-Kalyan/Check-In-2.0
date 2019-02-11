package gavs.first.com.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static gavs.first.com.home.Utils.host_ip;
import static gavs.first.com.home.Utils.url_authorize;
import static gavs.first.com.home.Utils.url_item_claim;

@SuppressLint("ValidFragment")
public class CustomDialogFragment extends DialogFragment {

    ProgressDialog dialog;
    EditText etTagId;
    long empId, itemId;
    Activity activity;
    int submitcount = 0;
    String tag = "", empProjectName = "", name = "";
    String parent = "";

    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";

    @SuppressLint("ValidFragment")
    public CustomDialogFragment(Activity activity, String parent) {
        this.activity = activity;
        this.parent = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_layout, container, false);


        SharedPreferences myprefs = getActivity().getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        name = myprefs.getString("name", "");
        empId = myprefs.getLong("empId", 0);


        // Do all the stuff to initialize your custom view


        etTagId = v.findViewById(R.id.etauthorize);
        etTagId.requestFocus();
        etTagId.setShowSoftInputOnFocus(true);
        etTagId.setClickable(true);
        etTagId.setFocusableInTouchMode(true);

        etTagId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tag = etTagId.getText().toString();
                if (tag.length() == 10) {

                    if (submitcount == 0) {
                        new PostMethod(tag).execute();
                        submitcount++;
                    }
                    //Toast.makeText(getContext(),tag,Toast.LENGTH_LONG).show();
                    etTagId.setText("");

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return v;
    }

    public void setValues(String itemid) {

        itemId = Long.parseLong(itemid);

    }


    public class PostMethod extends AsyncTask<String, String, Boolean> {

        String finalTag = "";

        public PostMethod(String tag) {
            finalTag = tag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Processing");
            dialog.show();
        }


        @Override
        protected Boolean doInBackground(String... strings) {


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
                String responseStr = response.body().string();

                JSONObject responseObj = new JSONObject(responseStr);

                empProjectName = responseObj.getString("projectName");


                if (response.code() == HttpURLConnection.HTTP_OK) {

                    return true;

                } else {

                    //Toast.makeText(getContext(), "Server not connected", Toast.LENGTH_LONG).show();
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
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
                                startActivity(i);

                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });

                // Toast.makeText(getContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                return false;

            } catch (IOException e) {
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


                // Toast.makeText(getApplicationContext(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();

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
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
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

            if (empProjectName.equals("Admin")) {
                dialog.dismiss();
                if (parent.equals("lost&found")) {
                    new PostMethodSubmitItem().execute("Admin");
                }
                if(parent.equals("umbrella")) {
                    Umbrella_Activity umbrella = new Umbrella_Activity();
                    
                }
                //Toast.makeText(getContext(),"Verified",Toast.LENGTH_LONG).show();
                getActivity().getFragmentManager().popBackStack();
                Intent i = new Intent(getActivity(), Home_Activity.class);
                startActivity(i);


            } else {
                //Toast.makeText(getActivity(), "Authentication Required", Toast.LENGTH_LONG).show();
                dialog.dismiss();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Access Denied");
                        // Setting Dialog Message
                        alertDialog.setMessage("Invalid Access Id");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
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


    class PostMethodSubmitItem extends AsyncTask<String, String, Boolean> {
        int responseCode;

        @Override
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

                url = new URL(url_item_claim);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());


                JSONObject obj = new JSONObject();

                obj.put("name", name);
                obj.put("lostEmpId", empId);
                obj.put("id", itemId);

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

                    Toast.makeText(getActivity(), "Server not connected", Toast.LENGTH_LONG).show();
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
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


                Toast.makeText(getActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return false;

            } catch (IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the Connectivity, kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


                dialog.dismiss();
                Toast.makeText(getActivity(), "Error : " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return false;
            } catch (JSONException e) {
                dialog.dismiss();

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
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(), Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
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
            if(responseCode == 200){
                Toast.makeText(activity,"Verified",Toast.LENGTH_LONG).show();
            }
        }


    }
}
