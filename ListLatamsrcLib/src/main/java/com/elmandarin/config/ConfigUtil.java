package com.elmandarin.config;

import android.content.Context;

import com.elmandarin.listlatamsrclib.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by: El Mandarin Sniff
 **/
public class ConfigUtil {
    Context context;


    // PASSWORD
    // PASSWORD

    //TOKEN PASSWORD
    //TOKEN PASSWORD
    public ConfigUtil(Context context) {
        this.context = context;
    }

    public String getVersion() {
        try {
            String version = getJSONConfig().getString("Version");
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String geNote() {
        try {
            String releaseNote = getJSONConfig().getString("ReleaseNotes");
            return releaseNote;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public JSONArray getServersArray() {
        try {
            if (getJSONConfig() != null) {
                JSONArray array = getJSONConfig().getJSONArray("Servers");
                return array;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private JSONObject getJSONConfig() {
        try {
            File file = new File(context.getFilesDir(), context.getString(R.string.LatamSRCnamejson));
            if (file.exists()) {
                String json_file = readStream(new FileInputStream(file));
                String json = AESCrypt.decrypt(context.getString(R.string.LatamSRCPASSWORD), json_file);
                return new JSONObject(json);
            } else {
                InputStream inputStream = context.getAssets().open(context.getString(R.string.LatamSRCPatch));
                String json = AESCrypt.decrypt(context.getString(R.string.LatamSRCPASSWORD), readStream(inputStream));
                return new JSONObject(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = new BufferedReader(new InputStreamReader(in));
            char[] buff = new char[1024];
            while (true) {
                int read = reader.read(buff, 0, buff.length);
                if (read <= 0) {
                    break;
                }
                sb.append(buff, 0, read);
            }
        } catch (Exception e) {

        }
        return sb.toString();
    }
}
