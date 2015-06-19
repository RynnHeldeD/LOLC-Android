package org.ema.lolcompanion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.ema.dialogs.ChampionTipDialogFragment;
import org.ema.dialogs.CooldownTimersDialogFragment;
import org.ema.dialogs.SecureDialogFragment;
import org.ema.fragments.AlliesFragment;
import org.ema.fragments.EnnemiesFragment;
import org.ema.fragments.TimersFragment;
import org.ema.utils.TimerButton;
import org.ema.utils.WebSocket;


public class CompanionActivity extends FragmentActivity implements ChampionTipDialogFragment.NoticeDialogListener, SecureDialogFragment.NoticeDialogListener, CooldownTimersDialogFragment.NoticeDialogListener {

    static final int NUMBER_OF_TABS = 3;
    private String[] tabs = {"Ennemies", "Timers", "Allies"};
    CompanionAdapter cpAdapter;
    ViewPager mPager;
    PagerTabStrip tab_strp;
    public static TimersFragment instance = null;
    public static CompanionActivity instanceCompanion = null;

    EnnemiesFragment ennemiesFragment = new EnnemiesFragment();
    AlliesFragment alliesFragment = new AlliesFragment();
    TimersFragment timerFragment = new TimersFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_companion);

        cpAdapter = new CompanionAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.companion_pager);
        mPager.setAdapter(cpAdapter);
        tab_strp = (PagerTabStrip) findViewById(R.id.companion_title_strip);
        tab_strp.setTextColor(Color.WHITE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        instance = timerFragment;
        instanceCompanion = this;
    }

    //Permet de Switcher entre les fragments
    public class CompanionAdapter extends FragmentPagerAdapter {
        public CompanionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ennemiesFragment;
                case 1:
                    return timerFragment;
                case 2:
                    return alliesFragment;
                default:
                    return ennemiesFragment;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

    //On cr�er une deuxi�me fonction avec un param�tre en plus car on ne peut pas passer de param�tres depuis la vue
    public void timerListener(View tbt) {
        timerListener(tbt, false, 0);
    }


    //This function handle the onclick (short) events for all buttons on the timer view
    public void timerListener(View tbt, boolean fromWebSocket, long delayOfTransfert) {
        TimerButton tbtn = (TimerButton) tbt;
        //Name of the clicked button => example : b21
        String IDButton = getResources().getResourceName(tbtn.getId());
        //button ID formated like "b12"
        String buttonID = IDButton.substring(IDButton.lastIndexOf("/") + 1);

        if(!tbtn.isTriggered()) {
            tbtn.setTriggered(true);
            timerFragment.simpleClickTimer(buttonID, 0, false, true);
        } else {
            timerFragment.simpleClickTimer(buttonID, 0, false, false);
        }

        // FUNCTION FOR DOUBLE CLICK (DISABLED)
        /*
        java.util.Date date= new java.util.Date();
        long now = date.getTime();
        long btnTimestp = tbtn.getClickedTimestamp();
        tbtn.setClickedTimestamp(now);

        if (!tbtn.isTriggered()) {
            tbtn.setTriggered(true);

            class PostponedClick implements Runnable {
                public TimerButton tbtn;
                public String buttonID;

                public PostponedClick(TimerButton tbtn, String buttonID) {
                    this.tbtn = tbtn;
                    this.buttonID = buttonID;
                }

                public void run() {
                    if (this.tbtn.isTriggered()) {
                        Log.v("DAO", this.buttonID + " simple click postponed");
                        timerFragment.simpleClickTimer(buttonID, 0, false, false);
                        this.tbtn.setTriggered(false);
                    }
                }
            }
            tbtn.postDelayed(new PostponedClick(tbtn, buttonID), 200);
        } else {
            if ((now <= btnTimestp + TimerButton.DELAY)) {
                //TODO : nettoyer le double clic
            }
            tbtn.setTriggered(false);
        }
        */
    }


    // DIALOG HANDLERS FOR ALL FRAGMENTS
    public void secureAppSharing(View v) {
        timerFragment.secureAppSharing(v);
    }

    public void showChampionTips(View v) {
        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.showChampionTips(v);
                break;
            case 2:
                alliesFragment.showChampionTips(v);
                break;
        }
    }

    //Show the dialod to set the cooldown reduction for a champion
    public void showCooldownReducers(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case R.id.b11:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(0).getChampion().getName());
                args.putString("ennemy", "b11");
                args.putInt("cdr",timerFragment.timerCdrMap.get("b12"));
                break;
            case R.id.b21:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(1).getChampion().getName());
                args.putString("ennemy", "b21");
                args.putInt("cdr",timerFragment.timerCdrMap.get("b22"));
                break;
            case R.id.b31:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(2).getChampion().getName());
                args.putString("ennemy", "b31");
                args.putInt("cdr",timerFragment.timerCdrMap.get("b32"));
                break;
            case R.id.b41:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(3).getChampion().getName());
                args.putString("ennemy", "b41");
                args.putInt("cdr",timerFragment.timerCdrMap.get("b42"));
                break;
            case R.id.b51:
                args.putString("name", ennemiesFragment.getSummonersOpponentsList().get(4).getChampion().getName());
                args.putString("ennemy", "b51");
                args.putInt("cdr",timerFragment.timerCdrMap.get("b52"));
                break;
        }
        timerFragment.showCooldownReducers(v, args);
    }

    //Go to AdvancedStat Activity
    public void showAdvancedStatistics(View v) {
        switch (mPager.getCurrentItem()) {
            case 0:
                //##############create a bitmap of activity
                /*ScrollView view = (ScrollView) ennemiesFragment.getActivity().findViewById(R.id.root_scroll_ennemies);
                Bitmap bm = getBitmapFromView(ennemiesFragment.getActivity().findViewById(R.id.root_activity_ennemy_load));
                Bitmap blurredBitmap = fastblur(bm, 12);
                BitmapDrawable bd = new BitmapDrawable(getResources(), blurredBitmap);
                //get the progress and unhide it
                LinearLayout loader = (LinearLayout) ennemiesFragment.getActivity().findViewById(R.id.loading_advstats);
                loader.setBackgroundDrawable(bd);
                view.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);*/
                ennemiesFragment.showAdvancedStatistics(v, true);
                break;
            case 2:
                //##############create a bitmap of activity
               /* ScrollView view_ally = (ScrollView) alliesFragment.getActivity().findViewById(R.id.root_scroll_allies);
                Bitmap bm_ally = getBitmapFromView(ennemiesFragment.getActivity().findViewById(R.id.root_activity_ally_load));
                Bitmap blurredBitmap_ally = fastblur(bm_ally, 12);
                BitmapDrawable bd_ally = new BitmapDrawable(getResources(), blurredBitmap_ally);

                //get the progress and unhide it
                LinearLayout loader_ally = (LinearLayout) alliesFragment.getActivity().findViewById(R.id.loading_advstats);
                loader_ally.setBackgroundDrawable(bd_ally);
                view_ally.setVisibility(View.GONE);
                loader_ally.setVisibility(View.VISIBLE);*/
               alliesFragment.showAdvancedStatistics(v, false);
                break;
        }
    }

    @Override
    public void onDialogNeutralClick(DialogFragment dialog, int idRessource) {

        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.onDialogNeutralClick(dialog, idRessource);
                break;
            case 2:
                alliesFragment.onDialogNeutralClick(dialog, idRessource);
                break;
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public Bitmap fastblur(Bitmap sentBitmap, int radius) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String passphrase) {
        timerFragment.onDialogPositiveClick(dialog, passphrase);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int cooldown, String ennemy_button_id) {
        timerFragment.onDialogPositiveClick(dialog, cooldown, ennemy_button_id);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        switch (mPager.getCurrentItem()) {
            case 0:
                ennemiesFragment.onDialogNegativeClick(dialog);
                break;
            case 2:
                alliesFragment.onDialogNegativeClick(dialog);
                break;
        }
    }

    //KeyDown event handler
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            Thread disconnectThread = new Thread(new Runnable() {
                public void run() {
                    WsEventHandling.disconnect();
                }
            });

            disconnectThread.start();
            launchMainActivity();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void handleDisconnection() {
        this.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
               if (!WebSocket.alreadyDisconnected) {
                   Toast.makeText(CompanionActivity.this, "Disconnected from server", Toast.LENGTH_SHORT).show();
               }
               try{
                   CompanionActivity.instance.cleanChannelSummary();
               } catch (Exception e){
                   Log.v("Websocket", "Erreur dans handleDisconnection au moment du CleanChannel:" + e.getMessage());
               }

               //On dis au websocket que maintenant ça ne sert plus a rien d'afficher des messages d'erreur car l'utilisteur sais qu'il est déconnecté
               WebSocket.alreadyDisconnected = true;
           }
       }
        );
    }

    public void reconnectionNotification() {
        this.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(CompanionActivity.this, "You've been reconnected", Toast.LENGTH_SHORT).show();
                   WebSocket.alreadyDisconnected = false;
               }
           }
        );
    }

    public void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
       // instance = timerFragment;
       // instanceCompanion = this;
    }

    @Override
    public void onPause() {
        super.onPause();
        //instance = null;
       // instanceCompanion = null;
    }
}
