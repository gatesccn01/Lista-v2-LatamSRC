package com.elmandarin.LatamSRC;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.elmandarin.config.ConfigUtil;
import com.elmandarin.listlatamsrclib.R;
import com.elmandarin.listlatamsrclib.constans.Settings;
import com.elmandarin.listlatamsrclib.servers.ServersActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * SOURCE CODE BY LATAMSRC
 * ECHO EL 29 DE MAYO DE 2024
 * EL MANDARIN SNIFF
 */
public class ElMandarinList extends LinearLayout {
    private static final String UPDATE_VIEWS = "MainUpdate";
    private TextView servername, serverinfo;
    private LinearLayout serverlayout;
    private ImageView serverimage;
    private ConfigUtil config;
    private Settings mConfig;

    /* private ElMandarinList (Context context){
         super (context);
         initilize (context);
     }*/
    public ElMandarinList(Context context, AttributeSet attributeSet) {
        super(context);
        initilize(context);
        setMainView();

    }

    private void initilize(Context context) {
        inflate(context, R.layout.latamsrcprincial, this);
        mConfig = new Settings(context);
        config = new ConfigUtil(context);
        SharedPreferences sPrefs = mConfig.getPrefsPrivate();

        /////////*****by LatamSRC PRos*/////
        IntentFilter filter = new IntentFilter();

        filter.addAction(UPDATE_VIEWS);
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(mActivityReceiver, filter);
        setMainView();
        /////////*****by LatamSRC PRos*/////
        serverlayout = findViewById(R.id.layout_mainservers);
        serverlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSettings = new Intent(getContext(), ServersActivity.class);
                //intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intentSettings);
            }
        });
        serverimage = findViewById(R.id.imagemainlayout);
        servername = findViewById(R.id.nombremainlayout);
        serverinfo = findViewById(R.id.infomainlayout);
    }


    private int mainposition() {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        return prefs.getInt("LastSelectedServer", 0);//bygatesccn
    }

    public void setMainView() {
        try {
            JSONObject object = config.getServersArray().getJSONObject(mainposition());
            String nombre = object.getString("Name");
            String info = object.getString("sInfo"); //tutomakebylatamsrc
            servername.setText(nombre);//bygatesccn
            if (info.isEmpty() || info == null) {
                serverinfo.setText(getContext().getString(R.string.app_name));
            } else {
                serverinfo.setText(info);//bygatesccn
            }

            setImagen(serverimage, object.getString("FLAG"));

        } catch (JSONException e) {

        } catch (Exception e) {

        }
    }

    public void setImagen(ImageView im, String nameo) throws Exception {
        InputStream inputStream = getContext().getAssets().open("flags/" + nameo + ".png");//bygatesccn
        im.setImageDrawable(Drawable.createFromStream(inputStream, nameo + ".png"));
    }
    /**
     * Para REcibirDatos O Cambios
     */
    /**
     * Para REcibirDatos O Cambios
     */
    private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(UPDATE_VIEWS) && context instanceof Activity && !((Activity) context).isFinishing()) {
                setMainView();
            }
        }
    };


    public static void updateMainViews(Context context) {
        Intent updateView = new Intent(UPDATE_VIEWS);
        LocalBroadcastManager.getInstance(context)
                .sendBroadcast(updateView);
    }


}
