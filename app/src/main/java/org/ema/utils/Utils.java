package org.ema.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ema.lolcompanion.MainActivity;
import org.ema.lolcompanion.R;
import org.ema.model.DAO.CurrentGameDAO;
import org.ema.model.business.Summoner;
import org.ema.model.interfaces.ISettableIcon;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Object;import java.lang.Override;import java.lang.String;import java.lang.Void;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

public class Utils {
    //Local cache for applications API.
    public static Hashtable<String,String> cache = new Hashtable<String,String>();
    public static int nbRequests = 0;

    //Please insert the url by imageView.setTag(yourUrl)
    //To call this async function:
    //new LoadImageFromUrl().execute(yourImageViewWithUrlInsideTheTag);
    public static class LoadImageFromUrl extends AsyncTask<Object, Void, Bitmap> {
        private ImageView imv;
        private String path;

        @Override
        protected Bitmap doInBackground(Object... params) {
            imv = (ImageView) params[0];
            path = imv.getTag().toString();

            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null && imv != null) {
                imv.setImageBitmap(result);
            }
        }
    }

    /**
     * Set the icon of a business class if its iconName is set.
     * Class must implements ISettableIcon interface.
     * Call with : new SetObjectIcon().execute(myObject);
     */
    public static class SetObjectIcon extends AsyncTask<Object, Void, Bitmap> {
        private String path = "";

        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap = null;
            if (!(params[0] instanceof ISettableIcon)) {
                return null;
            }

            String objectClass = params[0].getClass().getSimpleName().toString();
            switch (objectClass) {
                case "Summoner":
                    path = Constant.DDRAGON_SUMMONER_ICON_URI;
                    break;
                case "Spell":
                    path = Constant.DDRAGON_SUMMONER_SPELL_ICON_URI;
                    break;
                case "Champion":
                    path = Constant.DDRAGON_CHAMPION_ICON_URI;
                    break;
                case "Item":
                    path = Constant.DDRAGON_ITEMS_ICON_URI;
                    break;
                case "League":
                    path = Constant.DDRAGON_SCOREBOARD_ICON_URI;
                    break;
                default:
                    break;
            }
            path += ((ISettableIcon) params[0]).getIconName();

            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                ((ISettableIcon) params[0]).setIcon(bitmap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                // In case of 404, return a cute Poro
                URL url = null;
                try {
                    url = new URL(Constant.DDRAGON_SUMMONER_ICON_URI + "588.png");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(input);
                    if(objectClass.equals("Item")){
                        bitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, false);
                    }
                    ((ISettableIcon) params[0]).setIcon(bitmap);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            return bitmap;
        }
    }

    public static class RequestTask extends AsyncTask<String, String, String>{

        final CallbackMatcher callback;

        public RequestTask(CallbackMatcher callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            callback.stringCallback(responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }

    //Get document, return null if error or null
    public static String getDocument(String urlToRead) {
        //Return cache if exist.
        if(cache.get(urlToRead) != null) {
            //Log.v("CACHE",urlToRead);
            return cache.get(urlToRead);
        }

        nbRequests ++;
        //Log.v("REQUESTS",String.valueOf(nbRequests));

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(urlToRead));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                cache.put(urlToRead,responseString);
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            Log.v("REQUEST_FAILED",e.getMessage() + " " + urlToRead);
        } catch (IOException e) {
            Log.v("REQUEST_FAILED",e.getMessage() + " " + urlToRead);
        }
        return responseString;
    }

    //Get document, if null, do the request again while the limit exist
    public static String getDocumentAndCheck(String urlToRead, int limit) {
        //Return cache if exist.
        if(cache.get(urlToRead) != null) {
//            Log.v("CACHE",urlToRead);
            return cache.get(urlToRead);
        }

        nbRequests ++;
        //      Log.v("REQUESTS",String.valueOf(nbRequests));

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(urlToRead));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                cache.put(urlToRead,responseString);
                out.close();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            if(limit != 0) {
                Log.v("REQUEST_FAILED",limit  + ": Request " + urlToRead);
                SystemClock.sleep(2000);
                return getDocumentAndCheck(urlToRead, --limit);
            }
            else {
                Log.v("REQUEST_FAILED",e.getMessage());
            }
        } catch (IOException e) {
            if(limit != 0) {
                Log.v("REQUEST_FAILED",limit  + ": Request " + urlToRead);
                SystemClock.sleep(2000);
                return getDocumentAndCheck(urlToRead, --limit);
            }
            else {
                Log.v("REQUEST_FAILED",e.getMessage() + " " + urlToRead);
            }
        }
        return responseString;
    }

    public static class getStats extends AsyncTask<Object, Void, Integer> {
        private String path = "";

        @Override
        protected Integer doInBackground(Object... params) {
            Summoner summoner = (Summoner)params[0];
            CurrentGameDAO.getStatiscicsAndMostChampionsPlayed(summoner);
            return new Integer(1);
        }
    }

    public static class getPremades extends AsyncTask<Object, Void, Integer> {
        private String path = "";

        @Override
        protected Integer doInBackground(Object... params) {
            Summoner summoner = (Summoner)params[0];
            ArrayList<Summoner> summonerList = (ArrayList<Summoner>)params[1];
            CurrentGameDAO.getPremades(summoner, summonerList);
            return new Integer(1);
        }
    }

    public static class getRunesDetailled extends AsyncTask<Object, Void, Integer> {
        private String path = "";

        @Override
        protected Integer doInBackground(Object... params) {
            ArrayList<Summoner> summoners = (ArrayList<Summoner>)params[0];
            CurrentGameDAO.getRunesDetailled(summoners);
            return new Integer(1);
        }
    }

    public static class getMasteriesDetailled extends AsyncTask<Object, Void, Integer> {
        private String path = "";

        @Override
        protected Integer doInBackground(Object... params) {
            ArrayList<Summoner> summoners = (ArrayList<Summoner>)params[0];
            CurrentGameDAO.getMasteriesDetailled(summoners);
            return new Integer(1);
        }
    }
}
