package tesky.cep.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import tesky.cep.R;


public class CreateRoomActivity extends AppCompatActivity {
    TextView tvStatus;
    DataOutputStream socketOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_room);

        ligarServidor();

        tvStatus = findViewById(R.id.textView);

    }

    public void ligarServidor() {
        ConnectivityManager connManager;
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = connManager.getAllNetworks();


        for (Network minhaRede : networks) {
            NetworkInfo netInfo = connManager.getNetworkInfo(minhaRede);
            if (netInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                NetworkCapabilities propDaRede = connManager.getNetworkCapabilities(minhaRede);

                if (propDaRede.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                    WifiManager wifiManager = (WifiManager) getApplicationContext()
                            .getSystemService(WIFI_SERVICE);

                    String macAddress = wifiManager.getConnectionInfo().getMacAddress();
                    Log.v("PDM", "Wifi - MAC:" + macAddress);

                    int ip = wifiManager.getConnectionInfo().getIpAddress();
                    String ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));

                    Log.v("PDM", "Wifi - IP:" + ipAddress);
                    tvStatus.setText("IP da Sala:" + ipAddress);

                    Intent intent = new Intent(CreateRoomActivity.this, WaitingPlayer.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }



    public void desconectar() {
        try {
            if (socketOutput != null) {
                socketOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}