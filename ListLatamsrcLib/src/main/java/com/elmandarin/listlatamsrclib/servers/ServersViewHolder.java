package com.elmandarin.listlatamsrclib.servers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmandarin.listlatamsrclib.R;

import java.io.InputStream;


public class ServersViewHolder extends RecyclerView.ViewHolder {

    private TextView servername, serverinfo, serverpais;
    private ImageView serverimage,server_pr ;
    private Context context;
    private static final String namedefault = "default.png";
    private static final String extpng = ".png";
    private static final String extjpg = ".jpg";
    private ServersModel model;



    public ServersViewHolder(@NonNull View view, ServersAdapter.OnItemClickListener listener) {
        super(view);

        servername = view.findViewById(R.id.server_name);
        serverinfo = view.findViewById(R.id.server_info);
        serverimage = view.findViewById(R.id.server_image);
        serverpais = view.findViewById(R.id.server_pais);



        context = view.getContext();




        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model.getServerPosition());
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onItemLongClick(getAdapterPosition());
                return true;
            }
        });
    }

    public void bind(ServersModel server) {
        model = server;
        servername.setText(server.getServername());
        serverinfo.setText(server.getServerinfo());
        serverpais.setText(server.getServerPais());


        try {
            setImagen(serverimage, server.getServerFlag());
        } catch (Exception e) {
            serverimage.setImageResource(es.dmoral.toasty.R.drawable.ic_check_white_24dp);
        }





    }
    public void setImagen(ImageView im, String nameo) throws Exception {
        InputStream inputStream = context.getAssets().open("flags/" + nameo + extpng);
        im.setImageDrawable(Drawable.createFromStream(inputStream, nameo + extpng));
    }


}

