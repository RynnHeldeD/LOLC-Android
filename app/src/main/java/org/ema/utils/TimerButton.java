package org.ema.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

import com.makeramen.roundedimageview.RoundedImageView;

import org.ema.fragments.TimersFragment;
import org.ema.lolcompanion.CompanionActivity;
import org.ema.lolcompanion.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TimerButton extends RoundedImageView {
    public static final int DELAY = 200;
    protected Timer timer;
    //protected long clickedTimestamp;
    protected boolean triggered;
    private String url;

    public TimerButton(Context context) {
        super(context);
        this.timer = null;
        //this.clickedTimestamp = 0;
        this.triggered = false;
    }

    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.timer = null;
        //this.clickedTimestamp = 0;
        this.triggered = false;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TimerButton_onKeyLongPress: {
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The "+getClass().getCanonicalName()+":onKeyLongPress attribute cannot "
                                + "be used within a restricted context");
                    }

                    final String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnLongClickListener(new OnLongClickListener() {
                            private Method mHandler;

                            @Override
                            public boolean onLongClick(final View p_v) {
                                boolean result = false;
                                if (mHandler == null) {
                                    try {
                                        Class[] args = new Class[2];
                                        args[0] = String.class;
                                        args[1] = boolean.class;
                                        mHandler = TimersFragment.class.getMethod(handlerName, args);
                                    } catch (NoSuchMethodException e) {
                                        int id = getId();
                                        String idText = id == NO_ID ? "" : " with id '"
                                                + getContext().getResources().getResourceEntryName(
                                                id) + "'";
                                        throw new IllegalStateException("Could not find a method " +
                                                handlerName + "(View) in the activity "
                                                + getContext().getClass() + " for onKeyLongPress handler"
                                                + " on view " + TimerButton.this.getClass() + idText, e);
                                    }
                                }

                                try {
                                    Object[] params = new Object[2];
                                    int id = getId();
                                    params[0] = getContext().getResources().getResourceEntryName(id);
                                    params[1] = false;
                                    mHandler.invoke(CompanionActivity.instance, params);
                                    result = true;
                                } catch (IllegalAccessException e) {
                                    throw new IllegalStateException("Could not execute non "
                                            + "public method of the activity", e);
                                } catch (InvocationTargetException e) {
                                    throw new IllegalStateException("Could not execute "
                                            + "method of the activity", e);
                                }
                                return result;
                            }
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
        a.recycle();
    }

    public TimerButton(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.timer = null;
        //this.clickedTimestamp = 0;
        this.triggered = false;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimerButton);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TimerButton_onKeyLongPress: {
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The "+getClass().getCanonicalName()+":onKeyLongPress attribute cannot "
                                + "be used within a restricted context");
                    }

                    final String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnLongClickListener(new OnLongClickListener() {
                            private Method mHandler;

                            @Override
                            public boolean onLongClick(final View p_v) {
                                boolean result = false;
                                if (mHandler == null) {
                                    try {
                                        mHandler = getContext().getClass().getMethod(handlerName, View.class);
                                    } catch (NoSuchMethodException e) {
                                        int id = getId();
                                        String idText = id == NO_ID ? "" : " with id '"
                                                + getContext().getResources().getResourceEntryName(
                                                id) + "'";
                                        throw new IllegalStateException("Could not find a method " +
                                                handlerName + "(View) in the activity "
                                                + getContext().getClass() + " for onKeyLongPress handler"
                                                + " on view " + TimerButton.this.getClass() + idText, e);
                                    }
                                }

                                try {
                                    mHandler.invoke(getContext(), TimerButton.this);
                                    result = true;
                                } catch (IllegalAccessException e) {
                                    throw new IllegalStateException("Could not execute non "
                                            + "public method of the activity", e);
                                } catch (InvocationTargetException e) {
                                    throw new IllegalStateException("Could not execute "
                                            + "method of the activity", e);
                                }
                                return result;
                            }
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
        a.recycle();
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public long getCurrentTimestamp(){
        return  Long.parseLong(this.getTimer().getTimerTextView().getText().toString()) * 1000;
    }

    public void timerDelay(long delayToRetrench){
        String timerDelayString = this.getTimer().getTimerTextView().getText().toString();
        long currentTimestamp;
        try {
            currentTimestamp = Long.parseLong(timerDelayString) * 1000;
        } catch (NumberFormatException e){
            //Si on a appuyer sur un timer qui etait a une seconde, il se peux que le temps du traitement, il n'ai plus rien comme valeur
            //Du coup on le passe a delayToRetrench pour que a la creation du timer il soit a 0
            currentTimestamp = delayToRetrench;
        }

        if (currentTimestamp != 0) {
            if(this.getTimer() != null) {
                this.getTimer().cancel();
            }
            this.setTimer(new Timer(currentTimestamp - delayToRetrench, 1000, this.getTimer().getTimerTextView(), this));
            this.getTimer().start();
        }
    }

    /*
    public long getClickedTimestamp() {
        return this.clickedTimestamp;
    }

    public void setClickedTimestamp(long clickedTimestamp) {
        this.clickedTimestamp = clickedTimestamp;
    }
    */

    public boolean isTriggered() {
        return triggered;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public void loadIcon(String url) {
        if (this.url == null || !this.url.equals(url)) {
            new SetObjectIcon().execute(url);
            this.url = url;
        }
    }


    public class SetObjectIcon extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //connection.setDoInput(true);
                //connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
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
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            setImageBitmap(bitmap);
        }
    }
}
