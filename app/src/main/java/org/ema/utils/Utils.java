package org.ema.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.ema.model.business.Champion;
import org.ema.model.business.Item;
import org.ema.model.business.League;
import org.ema.model.business.Spell;
import org.ema.model.business.Summoner;

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

public class Utils {

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

    public static class SetIconFromUrl extends AsyncTask<Object, Void, Bitmap> {
        private String path = "";

        @Override
        protected Bitmap doInBackground(Object... params) {
            String objectClass = params[0].getClass().getSimpleName().toString();

            switch(objectClass){
                case "Summoner":
                    path = Constant.DDRAGON_SUMMONER_ICON_URI + ((Summoner)params[0]).getName();
                    break;
                case "Spell":
                    path = Constant.DDRAGON_SUMMONER_SPELL_ICON_URI + ((Spell)params[0]).getIconName();
                    break;
                case "Champion":
                    path = Constant.DDRAGON_CHAMPION_ICON_URI + ((Champion)params[0]).getIconName();
                    break;
                case "Item":
                    path = Constant.DDRAGON_ITEMS_ICON_URI + ((Item)params[0]).getName();
                    break;
                case "League":
                    path = Constant.DDRAGON_SCOREBOARD_ICON_URI + ((League)params[0]).getDivision();
                    break;
                default:
                    break;
            }

            try {
                URL url = new URL(path);
                Log.v("[DEBUG]", path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                switch(objectClass){
                    case "Spell":
                        ((Spell) params[0]).setIcon(bitmap);
                        break;
                    case "Champion":
                        ((Champion) params[0]).setIcon(bitmap);
                        break;
                    case "Item":
                        ((Item) params[0]).setIcon(bitmap);
                        break;
                    case "League":
                        ((League) params[0]).setIcon(bitmap);
                        break;
                    case "Summoner":
                    default:
                        break;
                }

                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
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

    public static String getDocument(String urlToRead) {
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
        return responseString;
    }
}
