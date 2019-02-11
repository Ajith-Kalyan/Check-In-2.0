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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static gavs.first.com.home.Utils.url_itemdetails_claim;

public class ClaimFragment extends SessionTimeOutBaseFragment {
    RecyclerView recyclerView;

Button search;
    public static final String PREFERENCES_FILE_NAME = "SIGNAGE";
    String ip="";
    String TAG="";

    double current_latitude, current_longitude;
    View view;
    List<ListItem> myListData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {

        view= inflater.inflate(R.layout.claimfragment_layout,container,false);


        SharedPreferences myprefs = getActivity().getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        ip=myprefs.getString("ip","");

        recyclerView = view.findViewById(R.id.recyclerView);
        search =view.findViewById(R.id.btnsearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new JsonParse().execute();
                //Toast.makeText(getContext(),ip,Toast.LENGTH_LONG).show();
            }
        });




     /*   ListItem listItem = new ListItem("Mobile","Samsung,Black");
        myListData.add(listItem);
        ListItem listItem2 = new ListItem("ID card","Shyam");
        myListData.add(listItem2);*/




        MyListAdapter adapter = new MyListAdapter(myListData, getContext(), getActivity());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        return view;
    }



    public class JsonParse extends AsyncTask<String, Void, List<ListItem>> {


        @Override
        protected List<ListItem> doInBackground(String... strings) {

            String server_response = null;
            URL url;
            HttpURLConnection urlConnection = null;

            try {
//todo add url

//                 url = new URL("http://10.0.14.22:8080/gavs/api/print/card");
                //url = new URL("http://" + ip + ":8081/gavs/api/lostitem/details");
                url = new URL(url_itemdetails_claim);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(server_response);


                        JSONArray jsonArray = null;

                        jsonArray = jsonResponse.getJSONArray("claim");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject r = jsonArray.getJSONObject(i);
                            //ListItem mylistItem = new ListItem("materialName","desc","id");
                            ListItem mylistItem = new ListItem(r.getString("materialName"), r.getString("desc"),r.getString("id"));
                            myListData.add(mylistItem);
                        }
                    }catch (JSONException e) {
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
                                        Intent i = new Intent(getActivity(),Home_Activity.class);
                                        startActivity(i);

                                    }
                                });
                                // Showing Alert Message
                                alertDialog.show();
                            }
                        });

                    }
                }
                    Log.v("Server response", server_response);
                    Log.v("Response code", String.valueOf(responseCode));
                } catch (ProtocolException e1) {

                e1.printStackTrace();


                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                        // Setting Dialog Title
                        alertDialog.setTitle("Error!");
                        // Setting Dialog Message
                        alertDialog.setMessage("An error occured in the server(Protocol), kindly report to Admin");
//                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.ic_verified);
                        // Setting OK Button
                        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(),Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });



            } catch (MalformedURLException e1) {
                e1.printStackTrace();

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
                                Intent i = new Intent(getActivity(),Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


            } catch (IOException e1) {
                e1.printStackTrace();

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
                                getActivity().getFragmentManager().popBackStack();
                                Intent i = new Intent(getActivity(),Home_Activity.class);
                                startActivity(i);
                            }
                        });
                        // Showing Alert Message
                        alertDialog.show();
                    }
                });


            }



               /* urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());



                try {


                    JSONObject obj = new JSONObject();

                    wr.writeBytes(obj.toString());
                    Log.e("JSON Input", obj.toString());
                    wr.flush();
                    wr.close();
                    urlConnection.connect();
                    InputStream stream = urlConnection.getInputStream();

                   BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line+"\n");
                        Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                    }


                    JSONObject jsonResponse = new JSONObject(line);

                    JSONArray jsonArray = jsonResponse.getJSONArray("claim");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject r = jsonArray.getJSONObject(i);


                        ListItem mylistItem = new ListItem("materialName","materialDesc","id");
                        myListData.add(mylistItem);


                    }



                    return myListData;

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            return myListData;

        }


        @Override
        protected void onPostExecute(List<ListItem> listItems) {
            super.onPostExecute(listItems);

            Log.e("Response", "200" );
            MyListAdapter adapter = new MyListAdapter(listItems, getContext(), getActivity());
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layout = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layout);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }


        }



    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();


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
                            getActivity().getFragmentManager().popBackStack();
                            Intent i = new Intent(getActivity(),Home_Activity.class);
                            startActivity(i);
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }
            });


        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();

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
                                    getActivity().getFragmentManager().popBackStack();
                                    Intent i = new Intent(getActivity(),Home_Activity.class);
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
        return response.toString();
    }
    }

