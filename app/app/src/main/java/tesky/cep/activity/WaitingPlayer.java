package tesky.cep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import tesky.cep.R;

public class WaitingPlayer extends AppCompatActivity {
    private String cep;
    String ipAddress;
    String character;

    ServerSocket welcomeSocket;
    DataOutputStream socketOutput;
    DataInputStream fromClient;

    TextView textViewRoomCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_player);

        Intent intent = this.getIntent();
        Bundle params = intent.getExtras();

        if (params != null) {
            ipAddress = params.getString("ip");
            character = params.getString("character");
        }

        textViewRoomCode = findViewById(R.id.textViewRoomCode);
        textViewRoomCode.setText("Código da sala: " + ipAddress);

        getRandomCEP();
        connectServer();
    }

    private void getRandomCEP() {
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        searchCEP();
                    }
                }
        );
        t.start();
    }

    private void searchCEP() {
        try {
            URL url = new URL("https://viacep.com.br/ws/CE/Fortaleza/Mont/json/");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            String[] result = new String[1];
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String responseLine;

                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                result[0] = response.toString();

                JSONArray arrayCEP = new JSONArray(result[0]);

                JSONObject respostaJSON = arrayCEP.getJSONObject(new Random().nextInt(arrayCEP.length() - 1));

                String cep = respostaJSON.getString("cep");
                String logradouro = respostaJSON.getString("logradouro");
                String cidade = respostaJSON.getString("localidade");
                String bairro = respostaJSON.getString("bairro");

                this.cep = cep;

                Log.v("WaitingPlayer", "CEP = " + cep
                        + "\nLOGRADOURO = " + logradouro
                        + "\nBAIRO = " + bairro
                        + "\nCIDADE = " + cidade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                connectServerCode();
            }
        });
        t.start();
    }

    public void connectServerCode() {
        try {
            welcomeSocket = new ServerSocket(9090);
            Socket connectionSocket = welcomeSocket.accept();

            Log.v("WaitingPlayer", "Device connected");

            fromClient = new DataInputStream(connectionSocket.getInputStream());
            socketOutput = new DataOutputStream(connectionSocket.getOutputStream());

            try {
                socketOutput.writeUTF("CEP:" + this.cep);
                if (this.character.equals("Morlock"))
                    socketOutput.writeUTF("CHARACTER:" + "Eloi");
                else
                    socketOutput.writeUTF("CHARACTER:" + "Morlock");

                socketOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(WaitingPlayer.this, Game.class);
            intent.putExtra("cep", this.cep);
            intent.putExtra("ip", this.ipAddress);
            intent.putExtra("character", this.character);
            intent.putExtra("type", "server");
            startActivity(intent);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
