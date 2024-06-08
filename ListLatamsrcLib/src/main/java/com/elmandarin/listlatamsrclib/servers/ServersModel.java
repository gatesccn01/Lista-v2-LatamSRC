package com.elmandarin.listlatamsrclib.servers;

import androidx.annotation.NonNull;

public class ServersModel {
    private String servername;
    private String serverinfo;
    private String imagename;
    private String serverpais;
    private String serverflag;
    private boolean iscustomserver;
    private int serverposition;

    private String feature;

    public ServersModel() {
    }

    @NonNull
    public String getServerFlag() {
        return this.serverflag;
    }


    @NonNull
    public String getServerPais() {
        return this.serverpais;
    }

    @NonNull
    public int getServerPosition() {
        return this.serverposition;
    }

    @NonNull
    public boolean IsCustomServer() {
        return this.iscustomserver;
    }

    @NonNull
    public String getServername() {
        return this.servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    @NonNull
    public String getServerinfo() {
        return this.serverinfo;
    }

    public String getFeature() {
        return feature;
    }


    public void setServerinfo(String serverinfo) {
        this.serverinfo = serverinfo;
    }

    @NonNull
    public String getServerimage() {
        return this.imagename;
    }

    public void setServerimage(String imagename) {
        this.imagename = imagename;
    }

    public void setIsCustomServer(boolean iscustomserver) {
        this.iscustomserver = iscustomserver;
    }

    public void setServerPais(String nuevopais) {
        this.serverpais = nuevopais;
    }

    public void setServerPosition(int nuevaposicion) {
        this.serverposition = nuevaposicion;
    }

    public void setServerFlag(String newflag) {
        this.serverflag = newflag;
    }
}

