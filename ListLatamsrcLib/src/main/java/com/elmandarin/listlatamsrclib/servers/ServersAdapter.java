package com.elmandarin.listlatamsrclib.servers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elmandarin.listlatamsrclib.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class ServersAdapter extends RecyclerView.Adapter<ServersViewHolder> {
    private List<ServersModel> servermodel;
    private List<ServersModel> listacompleta;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public ServersAdapter(List<ServersModel> mModel, Context c) {
        this.context = c;
        this.servermodel = mModel;
        listacompleta = new ArrayList<>();
    }

    public void setCompleteList(List<ServersModel> mModel) {
        listacompleta.addAll(mModel);
    }

    public void setOnItemClickListener(OnItemClickListener mlistener) {
        this.listener = mlistener;
    }

    @NonNull
    @Override
    public ServersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.servers_item, parent, false);

        return new ServersViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ServersViewHolder holder, int position) {
        ServersModel model = servermodel.get(position);
        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return servermodel.size();
    }

    public void filtrar(final String txtBuscar, Activity activity) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                int longitud = txtBuscar.length();
                if (txtBuscar.length() == 0) {
                    servermodel.clear();
                    servermodel.addAll(listacompleta);
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        List<ServersModel> collecion = listacompleta.stream()
                                .filter(i -> getData(i).toLowerCase().contains(txtBuscar.toLowerCase()) || getPais(i).toLowerCase().contains(txtBuscar.toLowerCase()))
                                .collect(Collectors.toList());
                        servermodel.clear();
                        servermodel.addAll(collecion);
                    } else {
                        for (ServersModel c : listacompleta) {
                            if (getData(c).toLowerCase().contains(txtBuscar.toLowerCase())) {
                                servermodel.add(c);
                            }
                        }
                    }
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        });
    }

    public void verlistacompleta(onListChanged changedlistener) {
        servermodel.clear();
        servermodel.addAll(listacompleta);
        notifyDataSetChanged();
        changedlistener.onChanged();
    }

    public void setlistaaz(List<ServersModel> nuevalista, onListChanged changedlistener) {
        listacompleta.clear();
        listacompleta.addAll(nuevalista);
        servermodel.clear();
        servermodel.addAll(nuevalista);
        notifyDataSetChanged();
        changedlistener.onChanged();
    }



    public void clearalllist() {
        servermodel.clear();
        listacompleta.clear();
        notifyDataSetChanged();
    }


    public interface onListChanged {
        void onChanged();
    }

    private String getData(ServersModel modelo) {
        try {
            return (modelo.getServername());
        } catch (Exception e) {
            return null;
        }
    }

    private String getPais(ServersModel modelo) {
        try {
            return (modelo.getServerPais());
        } catch (Exception e) {
            return null;
        }
    }

}

