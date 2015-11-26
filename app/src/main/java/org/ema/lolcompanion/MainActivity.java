/* Copyright � 2015
 * GHARBI Eddy
 * PARRENO Michel
 * VELTRI Constantin
 * NGUYEN Remy
 * GALLI Romain
 *
 * Cette �uvre est prot�g�e par le droit d�auteur et strictement r�serv�e � l�usage priv� du
 * client. Toute reproduction ou diffusion au profit de tiers, � titre
 * gratuit ou on�reux, de
 * tout ou partie de cette �uvre est strictement interdite et constitue une contrefa�on pr�vue
 * par les articles L 335-2 et suivants du Code de la propri�t�
 * intellectuelle. Les ayants-droits se
 * r�servent le droit de poursuivre toute atteinte � leurs droits de
 * propri�t� intellectuelle devant les
 * juridictions civiles ou p�nales.
 */

package org.ema.lolcompanion;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.*;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ema.dialogs.AboutDialogFragment;
import org.ema.dialogs.LolCompanionProgressDialog;
import org.ema.model.business.Summoner;
import org.ema.utils.LogUtils;
import org.ema.utils.SettingsManager;
import org.ema.utils.Utils;
import org.ema.utils.Constant;
import org.ema.model.DAO.*;

import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class MainActivity extends Activity {
    public final static String SUMMONER_NAME = "";

    // Preferences
    public static SettingsManager settingsManager = null;
    public boolean isFound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable async code on main
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Clear cache
        Utils.cache.clear();
        Utils.nbRequests = 0;

        // Initialize PreferencesManager
        MainActivity.settingsManager = new SettingsManager();
        PreferenceManager.getDefaultSharedPreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting summoner name if saved in preferences
        EditText summoner_name = (EditText) findViewById(R.id.summoner_name);
        summoner_name.setText(MainActivity.settingsManager.get(this, "summonerName"));

        //loading the league of legend equiv fonts
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/lol.ttf");
        //settings the textViews with the font
        TextView login = (TextView) findViewById(R.id.login);
        login.setTypeface(font);
        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setTypeface(font);
        TextView go = (TextView) findViewById(R.id.login_submit);
        go.setTypeface(font);

        Spinner spinner = (Spinner) findViewById(R.id.region_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lol_regions_array, R.layout.custom_spinner);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        if (MainActivity.settingsManager.get(this, "summonerRegion") != "")
            spinner.setSelection(Integer.parseInt(MainActivity.settingsManager.get(this, "summonerRegion")));

    }

    public void showAbout(View v) {
        DialogFragment dialog = new AboutDialogFragment();
        dialog.show(getFragmentManager(), "about");
    }

    public void openFacebook(View v){
        Intent facebookIntent;

        try {
            getApplicationContext().getPackageManager().getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/777956932303071")); //Trys to make intent with FB's URI
        } catch (Exception e) {
            facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/LoLCompanionTeam/")); //catches and opens a url to the desired page
        }

        startActivity(facebookIntent);
    }

    //Lauch the summoner search in LOL API and check validity
    public void lauchSummonerSearch(View view) {

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        //setting the toast
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);

        if ((System.currentTimeMillis() / 1000) > 1488834400) { //fin de la béta Mon, 06 Mar 2017 21:06:40 GMT
            text.setText(getResources().getString(R.string.beta_test_version_end));
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 40);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else if (mWifi.isConnected() == false && mMobile.isConnected() == false) {
            text.setText(getResources().getString(R.string.pending_network_error));
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 40);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else {
            //show a toast to inform user.
            text.setText(getResources().getString(R.string.pending_port_testing));
            Toast toasty = new Toast(getApplicationContext());
            toasty.setGravity(Gravity.BOTTOM, 0, 40);
            toasty.setDuration(Toast.LENGTH_SHORT);
            toasty.setView(layout);
            toasty.show();

            //hack android - we need to create a thread because UI is stopped by isLolCompanionServerReachable function
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000); // As I am using LENGTH_LONG in Toast
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
                                TextView text = (TextView) layout.findViewById(R.id.text);
                                if (!isLolCompanionServerReachable()) {
                                    text.setText(getResources().getString(R.string.pending_port_unreachable));
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM, 0, 40);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();
                                } else {
                                    text.setText(getResources().getString(R.string.pending_logging));
                                    Toast toast = new Toast(getApplicationContext());
                                    toast.setGravity(Gravity.BOTTOM, 0, 40);
                                    toast.setDuration(Toast.LENGTH_SHORT);
                                    toast.setView(layout);
                                    toast.show();
                                    new CheckUserBackgroundTask(MainActivity.this).execute();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        }
    }

    //Check if lolcompanion cache and websocket servers are reachable before connecting.
    public boolean isLolCompanionServerReachable() {
        String cacheServer = getResources().getString(R.string.cache_ip);
        int cachePort = Integer.parseInt(getResources().getString(R.string.cache_port));
        String WSServer = getResources().getString(R.string.websocket_ip);
        int WSPort = Integer.parseInt(getResources().getString(R.string.websocket_port));
        int lolc_timeout = Integer.parseInt(getResources().getString(R.string.lolcompanion_server_timeout_millisecond));

        try {
            Socket socketCache = new Socket();
            Socket socketWS = new Socket();
            socketCache.connect(new InetSocketAddress(cacheServer, cachePort), lolc_timeout);
            socketWS.connect(new InetSocketAddress(WSServer, WSPort), lolc_timeout);
            socketCache.close();
            socketWS.close();
            LogUtils.LOGV("MIC", "Port open OK");
            return true;
        } catch (ConnectException ce) {
            LogUtils.LOGV("MIC", "Port open NONOK");
            ce.printStackTrace();
            return false;
        } catch (Exception ex) {
            Log.v("MIC", "Port open NONOK");
            ex.printStackTrace();
            return false;
        }
    }

    class CheckUserBackgroundTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;
        private Summoner user = null;

        public CheckUserBackgroundTask(Context ctx) {
            progressDialog = LolCompanionProgressDialog.getCompanionProgressDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Spinner spinner = (Spinner) findViewById(R.id.region_spinner);
            int position = spinner.getSelectedItemPosition();

            //Init Pending Room
            Intent intent = new Intent(MainActivity.this, PendingRoomActivity.class);

            if (result) {
                intent.putExtra(SUMMONER_NAME, user.getName());
                intent.putExtra("USER", user);
                MainActivity.settingsManager.set(MainActivity.this, "summonerName", user.getName());
                MainActivity.settingsManager.set(MainActivity.this, "summonerRegion", String.valueOf(position));
                startActivity(intent);
            } else {
                //setting the toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Player not found on " + spinner.getSelectedItem().toString().split(" ")[0]);
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 40);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }

            progressDialog.hide();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            EditText summonerTextView = (EditText) MainActivity.this.findViewById(R.id.summoner_name);
            String summonerName = summonerTextView.getText().toString();

            //saving the region for further connections
            Spinner spinner = (Spinner) findViewById(R.id.region_spinner);
            int position = spinner.getSelectedItemPosition();
            Constant.setRegion(Constant.regionsFromViewHashtable.get(spinner.getSelectedItem().toString()));
            user = SummonerDAO.getSummoner(summonerName);
            return (user != null);
        }
    }
}
