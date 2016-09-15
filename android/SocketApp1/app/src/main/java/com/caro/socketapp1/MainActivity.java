package com.caro.socketapp1;

import android.app.Fragment;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //apenas se inicia la aplicacion intenta conectar
        connectWebSocket();
        //en esta variable se guardan ciertos datos cuando una app se cierra intempestivamente
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    //carga el menu derecho
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        //???
        return super.onOptionsItemSelected(item);
    }
    //clase para el fragmento
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        //metodo que infla el fragmento con su vista
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_principal, container, false);
            return rootView;
        }
    }
    //metodo que hace la conexion socket
    private void connectWebSocket() {
        //validamos la URL socket
        URI uri;
        try {
            uri = new URI("ws://websockethost:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        //instanciamos el objeto mWebSocketClient y definimos sus metodos
        mWebSocketClient = new WebSocketClient(uri) {
            //cuando hubo una conexion correcta, un handshake exitoso??
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Conexion exitosa");
                mWebSocketClient.send("Hola desde " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            //cuando recibimos un mensaje??
            @Override
            public void onMessage(String s) {
                final String message = s;
                //Un metodo que pretende modificar la barra del UI del sistema?? y modifica el fragment
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.messages);
                        textView.setText(textView.getText() + "\n" + message);
                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Conexion socket cerrada " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error al conectar socket " + e.getMessage());
            }
        };

        //intentamos hacer conexion socket
        mWebSocketClient.connect();
    }
    //metodo con el que enviamos el mensaje, usamos el metodo interno send
    public void sendMessage(View view) {
        EditText editText = (EditText)findViewById(R.id.message);
        mWebSocketClient.send(editText.getText().toString());
        editText.setText("");
    }
}
