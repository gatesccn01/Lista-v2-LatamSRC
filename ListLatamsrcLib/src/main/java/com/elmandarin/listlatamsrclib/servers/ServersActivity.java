package com.elmandarin.listlatamsrclib.servers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.elmandarin.LatamSRC.ElMandarinList;
import com.elmandarin.config.ConfigUtil;
import com.elmandarin.listlatamsrclib.R;
import com.elmandarin.listlatamsrclib.constans.Settings;
import com.elmandarin.logger.SkStatus;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import es.dmoral.toasty.Toasty;

public class ServersActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView serversrecycler;
    private List<ServersModel> servidores;
    private List<ServersModel> defaultlist;
    private ServersAdapter adaptador;
    private ConfigUtil config;
    private Settings mConfig;
    private ImageButton clear;
    private SearchView searchedit;
    private ScrollView scrolldialog;
    private InputMethodManager inputmetodo;
    private boolean escribiendo = false;
    private boolean isSearchViewVisible = true;
    private boolean issearchexpanded = false;
    private AlertDialog.Builder builder;
    private LinearLayout searchlayout;
    private ActionBar actionbar;
    private Toolbar toolbar;
    private AlertDialog dialog;
    private ViewPager vp;
    private TabLayout tabs;
    private AppBarLayout barlayout;
    private boolean isSSL;
    private boolean isSSH;
    private static final String[] tabTitle = {"SERVIDORES","SABER MAS"};
    private boolean isSSLPayload, slw, drct, drcth;
    TextInputEditText sName,sFlag,sHost,sPort,sslPort,rHost,rPort,user,pass,sinfo,usersurl,checkuser,pub,ns,dns,cPayload,sni,sPais;
    CheckBox drc,usessh,usessl,usesslpayload,slwd;
    private MenuItem search;
    private MenuItem  ordenardefault, ordenaraz, ordencompania;
    private int positionlong;
    private LottieAnimationView loading;
    private boolean iseditando = false;
    private boolean isbarhide = false;
    private LinearLayout payloadL;
    private EditText etName, etEmail, etPassword;
    private TextView tvNameError, tvEmailError, tvPasswordError, tvColor;
    private CardView frameOne, frameTwo, frameThree, frameFour;
    private CardView btnRegister;
    private boolean isAtLeast8 = false, hasUppercase = false, hasNumber = false, hasSymbol = false, isRegistrationClickable = false;

    private LinearLayout sniL;
    private int posice;
    private LinearLayout dnsLayout;
    private ExecutorService executor;

    private Handler handler;

    private static final int ORDENADOAZ = 1;
    private static final int ORDENADODEFAULT = 0;
    private static final int ORDENARCOMPANIA = 2;
    private int tipoordenado;
    private String compania;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    private static final int RC_APP_UPDATE = 100;
    private SearchView containersearchView;

    private EditText token1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);
        mConfig = new Settings(this);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        loading = findViewById(R.id.servers_animation);
        loading.loop(true);
        loading.playAnimation();



        toolbar = findViewById(R.id.toolbar_main);
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        compania = manager.getSimOperatorName();
        prefs = mConfig.getPrefsPrivate();
        edit = prefs.edit();




        tipoordenado = prefs.getInt(Settings.ORDENADO, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionbar = getSupportActionBar();
        barlayout = findViewById(R.id.bar_main);
        inputmetodo = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        config = new ConfigUtil(this);

        serversrecycler = findViewById(R.id.recycler_servers);
        servidores = new ArrayList<>();
        defaultlist = new ArrayList<>();
        if (tipoordenado == ORDENADODEFAULT) {
            setlista(new onListLoaded() {
                @Override
                public void onLoaded() {
                    adaptador.setCompleteList(servidores);
                    loading.cancelAnimation();
                    loading.setVisibility(View.GONE);
                }
            });
        } else if (tipoordenado == ORDENADOAZ) {
            setlistaaz(new onListLoaded() {
                @Override
                public void onLoaded() {
                    adaptador.setCompleteList(servidores);
                    loading.cancelAnimation();
                    loading.setVisibility(View.GONE);
                }
            });
        } else if (tipoordenado == ORDENARCOMPANIA) {
            if (compania != null || !compania.isEmpty()) {
                setlistacompania(new onListLoaded() {
                    @Override
                    public void onLoaded() {
                        adaptador.setCompleteList(servidores);
                        loading.cancelAnimation();
                        loading.setVisibility(View.GONE);
                    }
                });
            } else {
                Toasty.warning(ServersActivity.this, "No se pudo obtener el nombre de la compañia, mostrando lista por defecto");
                setlista(new onListLoaded() {
                    @Override
                    public void onLoaded() {
                        adaptador.setCompleteList(servidores);
                        loading.cancelAnimation();
                        loading.setVisibility(View.GONE);
                    }
                });
            }
        }
        serversrecycler.setLayoutManager(new LinearLayoutManager(this));
        serversrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int totalItemCount, lastVisibleItem;
            boolean isLoading = false;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (inputmetodo.isActive() && escribiendo || issearchexpanded) {
                    inputmetodo.hideSoftInputFromWindow(serversrecycler.getWindowToken(), 0);
                }
            }
        });
        adaptador = new ServersAdapter(servidores, this);
        adaptador.setOnItemClickListener(new ServersAdapter.OnItemClickListener() {

            @Override
            public void onItemLongClick(int position) {

                ServersModel modelservers = servidores.get(position);


            }

            @Override
            public void onItemClick(int posicion) {
                loadserverdata(posicion);
            }
        });



        serversrecycler.setAdapter(adaptador);


        barlayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    isbarhide = true;
                } else {
                    isbarhide = false;
                }
            }
        });
    }

    private void mostraraz() {
        loading.playAnimation();
        loading.setVisibility(View.VISIBLE);
        Comparator<ServersModel> comparator = new Comparator<ServersModel>() {
            @Override
            public int compare(ServersModel item1, ServersModel item2) {
                return item1.getServername().compareToIgnoreCase(item2.getServername());
            }
        };

        List<ServersModel> ordenada = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ordenada = servidores.stream().sorted(comparator).collect(Collectors.toList());
        }
        adaptador.setlistaaz(ordenada, new ServersAdapter.onListChanged() {
            @Override
            public void onChanged() {
                loading.setVisibility(View.GONE);
                loading.cancelAnimation();
            }
        });
        serversrecycler.scrollToPosition(0);
    }




    private void saveSpinner(int position){
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("LastSelectedServer", position);
        edit.apply();
    }
    private void loadserverdata(int pos1) {
        try {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
            SharedPreferences.Editor edit = prefs.edit();

            String ssh_server = config.getServersArray().getJSONObject(pos1).getString("ServerIP");
            String remote_proxy = config.getServersArray().getJSONObject(pos1).getString("ProxyIP");
            String proxy_port = config.getServersArray().getJSONObject(pos1).getString("ProxyPort");
            String ssh_user = config.getServersArray().getJSONObject(pos1).getString("ServerUser");
            String ssh_password = config.getServersArray().getJSONObject(pos1).getString("ServerPass");
            String ssh_port = config.getServersArray().getJSONObject(pos1).getString("ServerPort");
            String ssl_port = config.getServersArray().getJSONObject(pos1).getString("SSLPort");
            String payload = config.getServersArray().getJSONObject(pos1).getString("Payload");
            String sni = config.getServersArray().getJSONObject(pos1).getString("SNI");
            String chaveKey = config.getServersArray().getJSONObject(pos1).getString("Slowchave");
            String serverNameKey = config.getServersArray().getJSONObject(pos1).getString("Nameserver");
            String dnsKey = config.getServersArray().getJSONObject(pos1).getString("Slowdns");
            String udpserver = config.getServersArray().getJSONObject(pos1).getString("udpserver");
            String udpauth = config.getServersArray().getJSONObject(pos1).getString("udpauth");
            String udpobfs = config.getServersArray().getJSONObject(pos1).getString("udpobfs");
            String udpbuffer = config.getServersArray().getJSONObject(pos1).getString("udpbuffer");
            String udpdown = config.getServersArray().getJSONObject(pos1).getString("udpdown");
            String udpup = config.getServersArray().getJSONObject(pos1).getString("udpup");
            String authlatamsrc = config.getServersArray().getJSONObject(pos1).getString("apilatamsrcv2ray");
            String gatesccn = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            String v2rayJson = config.getServersArray().getJSONObject(pos1).getString("v2rayJson");



            edit.putString(Settings.SERVIDOR_KEY, ssh_server);
            edit.putString(Settings.PROXY_IP_KEY, remote_proxy);
            edit.putString(Settings.PROXY_PORTA_KEY, proxy_port);
            edit.putString(Settings.APILATAMIP, authlatamsrc);


            boolean sslType = config.getServersArray().getJSONObject(pos1).getBoolean("isSSL");
            boolean sslpayload = config.getServersArray().getJSONObject(pos1).getBoolean("isPayloadSSL");
            boolean inject = config.getServersArray().getJSONObject(pos1).getBoolean("isInject");
            boolean direct = config.getServersArray().getJSONObject(pos1).getBoolean("isDirect");
            boolean slow = config.getServersArray().getJSONObject(pos1).getBoolean("isSlow");
            boolean isudp = config.getServersArray().getJSONObject(pos1).getBoolean("isudp");
            boolean iv2ray = config.getServersArray().getJSONObject(pos1).getBoolean("iv2ray");


            //SSH DIRECT
            if (direct) {
                prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT).apply();
                prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
                prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssh_port).apply();
                prefs.edit().putString(Settings.PROXY_IP_KEY, remote_proxy).apply();
                prefs.edit().putString(Settings.PROXY_PORTA_KEY, proxy_port).apply();
                prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
            }

            //SSH PROXY
            if (inject) {
                prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY).apply();
                prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
                prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssh_port).apply();
                prefs.edit().putString(Settings.PROXY_IP_KEY, remote_proxy).apply();
                prefs.edit().putString(Settings.PROXY_PORTA_KEY, proxy_port).apply();
                prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
            }
            //SSH SSL
            if (sslType) {
                prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL).apply();
                prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
                prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssl_port).apply();
                prefs.edit().putString(Settings.PROXY_IP_KEY, remote_proxy).apply();
                prefs.edit().putString(Settings.PROXY_PORTA_KEY, proxy_port).apply();
                prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
                prefs.edit().putString(Settings.CUSTOM_SNI, sni).apply();
            }
            //SSL PAYLOAD
            if (sslpayload) {
                prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSL_PAYLOAD).apply();
                prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
                prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssl_port).apply();
                prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
                prefs.edit().putString(Settings.CUSTOM_SNI, sni).apply();
            }
            //SLOW DIRECT
            if (slow) {
                prefs.edit().putString(Settings.CHAVE_KEY, chaveKey).apply();
                prefs.edit().putString(Settings.NAMESERVER_KEY, serverNameKey).apply();
                prefs.edit().putString(Settings.DNS_KEY, dnsKey).apply();
                prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
                prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssh_port).apply();
                prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SLOWDNS).apply();
            }
            if (isudp) {

                prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_UDP).apply();
                prefs.edit().putString(Settings.UDP_BUFFER, udpbuffer).apply();
                prefs.edit().putString(Settings.UDP_SERVER, udpserver).apply();
                prefs.edit().putString(Settings.UDP_AUTH, udpauth).apply();
                prefs.edit().putString(Settings.UDP_OBFS, udpobfs).apply();
                prefs.edit().putString(Settings.UDP_DOWN, udpdown).apply();
                prefs.edit().putString(Settings.UDP_UP, udpup).apply();
            }
            if (iv2ray) {
                edit.putString(Settings.V2RAY_JSON, v2rayJson);
                edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_V2RAY);
                edit.apply();
            }

            edit.apply();
            saveSpinner(pos1);
            ElMandarinList.updateMainViews(getApplicationContext());
            finish();

        } catch (Exception e) {
            SkStatus.logInfo(e.getMessage());
        }
    }

    private void setlista(onListLoaded listener) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < config.getServersArray().length(); i++) {
                        JSONObject servers = config.getServersArray().getJSONObject(i);
                        ServersModel modelo = new ServersModel();
                        String nombre = servers.getString("Name");
                        String info = servers.getString("sInfo");
                        String pais = servers.getString("sPais");
                        String flag = servers.getString("FLAG");

                        if (info.isEmpty() || info == null) {
                            modelo.setServerinfo(getString(R.string.app_name));
                        } else {
                            modelo.setServerinfo(info);
                        }
                        modelo.setServername(nombre);
                        modelo.setServerPais(pais);
                        modelo.setServerFlag(flag);
                        modelo.setServerPosition(i);
                        servidores.add(modelo);
                        ServersActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adaptador.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (JSONException e) {
                    ServersActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ServersActivity.this, "JSON Error Severs Activity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }


            };
        });}

    private void setlistaaz(onListLoaded listener) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < config.getServersArray().length(); i++) {
                        JSONObject servers = config.getServersArray().getJSONObject(i);
                        ServersModel modelo = new ServersModel();
                        String nombre = servers.getString("Name");
                        String info = servers.getString("sInfo");
                        String pais = servers.getString("sPais");
                        String flag = servers.getString("FLAG");

                        if (info.isEmpty() || info == null) {
                            modelo.setServerinfo(getString(R.string.app_name));
                        } else {
                            modelo.setServerinfo(info);
                        }
                        modelo.setServername(nombre);
                        modelo.setServerPais(pais);
                        modelo.setServerFlag(flag);
                        modelo.setServerPosition(i);
                        servidores.add(modelo);
                        Collections.sort(servidores, new Comparator<ServersModel>() {
                            @Override
                            public int compare(ServersModel item1, ServersModel item2) {
                                return item1.getServername().compareToIgnoreCase(item2.getServername());
                            }
                        });
                        ServersActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adaptador.notifyDataSetChanged();
                            }
                        });

                    }
                    ServersActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoaded();
                        }
                    });
                } catch (JSONException e) {
                    //  Toast.makeText(ServersActivity.this, e.getMessage(), 1, true).show();
                }
            }
        });
    }

    private void setlistacompania(onListLoaded listener) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < config.getServersArray().length(); i++) {
                        JSONObject servers = config.getServersArray().getJSONObject(i);
                        ServersModel modelo = new ServersModel();
                        String nombre = servers.getString("Name");
                        String info = servers.getString("sInfo");
                        String pais = servers.getString("sPais");
                        String flag = servers.getString("FLAG");


                        if (nombre.toLowerCase().contains(compania.toLowerCase())) {
                            if (info.isEmpty() || info == null) {
                                modelo.setServerinfo(getString(R.string.app_name));
                            } else {
                                modelo.setServerinfo(info);
                            }
                            modelo.setServername(nombre);
                            modelo.setServerPais(pais);
                            modelo.setServerPosition(i);
                            modelo.setServerFlag(flag);
                            servidores.add(modelo);
                            ServersActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adaptador.notifyDataSetChanged();
                                }
                            });
                        } else {
                            continue;
                        }

                    }
                    ServersActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onLoaded();
                        }
                    });
                } catch (JSONException e) {
                    // Toast.makeText(ServersActivity.this, e.getMessage(), 1, true).show();
                }
            }
        });
    }




    @Override
    public boolean onQueryTextSubmit(String s) {
        escribiendo = false;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        escribiendo = true;
        adaptador.filtrar(s, ServersActivity.this);
        return false;
    }


    private void hidetoolsmenu() {
        toolbar.setTitle("Servidores");
        toolbar.setSubtitle("");
        search.setVisible(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.servers_menu, menu);
        search = menu.findItem(R.id.menu_search);
        searchedit = (SearchView) search.getActionView();
        ordenardefault = menu.findItem(R.id.ordenardefault);
        ordenaraz = menu.findItem(R.id.ordenaraz);
        ordencompania = menu.findItem(R.id.ordenarsim);
        if (tipoordenado == ORDENADODEFAULT) {
            ordenardefault.setChecked(true);
        } else if (tipoordenado == ORDENADOAZ) {
            ordenaraz.setChecked(true);
        } else if (tipoordenado == ORDENARCOMPANIA) {
            ordencompania.setChecked(true);
        }
        searchedit.setQueryHint("Buscar");
        searchedit.setOnQueryTextListener(this);
        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // El SearchView se expandió, no hacer nada aquí
                issearchexpanded = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                issearchexpanded = false;
                if (inputmetodo.isActive()) {
                    inputmetodo.hideSoftInputFromWindow(serversrecycler.getWindowToken(), 0);
                }

                if (servidores.size() == 0) {
                    searchedit.setQuery("", false);
                    adaptador.verlistacompleta(new ServersAdapter.onListChanged() {
                        @Override
                        public void onChanged() {

                        }
                    });
                }
                return true;
            }
        });adaptador.clearalllist();
        setlistaaz(new onListLoaded() {
            @Override
            public void onLoaded() {
                adaptador.setCompleteList(servidores);
                loading.setVisibility(View.GONE);
                loading.cancelAnimation();
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.menu_cancelar) {
            iseditando = false;
            hidetoolsmenu();
        } else if (itemId == R.id.ordenaraz) {
            if (item.isChecked()) {
                item.setChecked(false);

            } else {
                loading.playAnimation();
                loading.setVisibility(View.VISIBLE);
                item.setChecked(true);
                edit.putInt(Settings.ORDENADO, ORDENADOAZ);
                edit.apply();
                adaptador.clearalllist();
                setlistaaz(new onListLoaded() {
                    @Override
                    public void onLoaded() {
                        adaptador.setCompleteList(servidores);
                        loading.setVisibility(View.GONE);
                        loading.cancelAnimation();
                    }
                });
            }
        } else if (itemId == R.id.ordenardefault) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                loading.playAnimation();
                loading.setVisibility(View.VISIBLE);
                item.setChecked(true);
                edit.putInt(Settings.ORDENADO, ORDENADODEFAULT);
                edit.apply();
                adaptador.clearalllist();
                setlista(new onListLoaded() {
                    @Override
                    public void onLoaded() {
                        adaptador.setCompleteList(servidores);
                        loading.setVisibility(View.GONE);
                        loading.cancelAnimation();
                    }
                });
            }
        } else if (itemId == R.id.ordenarsim) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                loading.playAnimation();
                loading.setVisibility(View.VISIBLE);
                item.setChecked(true);
                edit.putInt(Settings.ORDENADO, ORDENARCOMPANIA);
                edit.apply();
                adaptador.clearalllist();
                setlistacompania(new onListLoaded() {
                    @Override
                    public void onLoaded() {
                        adaptador.setCompleteList(servidores);
                        loading.setVisibility(View.GONE);
                        loading.cancelAnimation();
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private String textfromedit(TextInputEditText texto) {
        return texto.getText().toString().trim();
    }


    @Override
    public void onBackPressed() {
        if (iseditando != false) {
            hidetoolsmenu();
            ServersActivity.this.iseditando = false;

        } else if (isbarhide) {
            barlayout.setExpanded(true);
        } else {
            super.onBackPressed();
        }
        finish();
//        Intent intentservers = new Intent(this, ServersActivity.class);
//        startActivity(intentservers);
    }

    public interface onListLoaded {
        void onLoaded();
    }

    private String gettextfromedit(TextInputEditText edit) {
        return edit.getEditableText().toString().trim();
    }
}


