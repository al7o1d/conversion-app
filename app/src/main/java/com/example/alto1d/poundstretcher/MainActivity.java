package com.example.alto1d.poundstretcher;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends ActionBarActivity {

    private Spinner fromSpinner;
    private Spinner toSpinner;
    public static String from = "";
    public static String to = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        ArrayAdapter<CharSequence> fromSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        addItemsToSpinner(fromSpinner, fromSpinnerAdapter);
        addListenerToFromSpinner();
        /*
        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        ArrayAdapter<CharSequence> toSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.currencies, android.R.layout.simple_spinner_item);
        addItemsToSpinner(toSpinner, toSpinnerAdapter);
        addListenerToToSpinner();*/
    }

    public void addItemsToSpinner(Spinner sp, ArrayAdapter aa) {
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(aa);
    }

    public void addListenerToFromSpinner() {
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                if (itemSelected.equals("Great British Pound")) {
                    from = "GBP";

                } else if (itemSelected.equals("United States Dollar")) {
                    from = "USD";

                } else if (itemSelected.equals("Euro")) {
                    from = "EUR";

                } else if (itemSelected.equals("Russian Ruble")) {
                    from = "RUB";

                } else if (itemSelected.equals("Bulgarian Lev")) {
                    from = "BGN";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO something later
            }
        });
    }

    public void addListenerToToSpinner() {
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelected = parent.getItemAtPosition(position).toString();
                switch (itemSelected) {
                    case "Great British Pound":
                        to = "GBP";
                        break;
                    case "United States Dollar":
                        to = "USD";
                        break;
                    case "Euro":
                        to = "EUR";
                        break;
                    case "Russian Ruble":
                        to = "RUB";
                        break;
                    case "Bulgarian Lev":
                        to = "BGN";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO something later
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            System.out.println(Locale.getDefault().getLanguage());
            if(Locale.getDefault().getLanguage() == "bg") {
                Locale locale = new Locale("en");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration config = new Configuration();
                Locale.setDefault(locale);
                config.locale = locale;
                //this.getApplicationContext().getResources().updateConfiguration(config, null);
                res.updateConfiguration(config, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
                finish();
                return true;
            } else {
                Locale locale = new Locale("bg");
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration config = new Configuration();
                Locale.setDefault(locale);
                config.locale = locale;
                //this.getApplicationContext().getResources().updateConfiguration(config, null);
                res.updateConfiguration(config, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                startActivity(refresh);
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickConvert(View v) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;
        if(connected) {
            ConvertFragment fr = new ConvertFragment();
            fr.show(getFragmentManager(), "theFragment");
        } else
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public String getData()  {
        EditText toConvert = (EditText) findViewById(R.id.editText);
        String toConvertString = String.valueOf(toConvert.getText());
        if(TextUtils.isDigitsOnly(toConvertString)) {
            double toConvertDouble = Double.parseDouble(toConvertString);
            double rate = 0;
            try {
                rate = new FetchRate().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            double result = rate*toConvertDouble;
            //DecimalFormat df = new DecimalFormat("#.0000");
            String finalResult = String.valueOf(result);
            return finalResult;
        }
        else return null;
    }
}

class FetchRate extends AsyncTask<Void, Void, Double> {

    @Override
    protected Double doInBackground(Void... params) {
        URL url = null;
        try {
            url = new URL(
                    "http://jsonrates.com/get/?"+
                            "from=" + MainActivity.from +
                            "&to=BGN"  +
                            "&apiKey=jr-dca5843d06164ba39c97dfb92225c295"
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String data = null;
        try {
            data = IOUtils.toString(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject json = null;
        try {
            json = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Double rate = null;
        try {
            if (json != null) {
                rate = json.getDouble("rate");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rate;
    }

    @Override
    protected void onPostExecute(Double aDouble) {
        super.onPostExecute(aDouble);
    }
}