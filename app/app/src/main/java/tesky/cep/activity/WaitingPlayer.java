package tesky.cep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import tesky.cep.R;

public class WaitingPlayer extends AppCompatActivity {
    private int cep;
    private int cepDigits;

    ServerSocket welcomeSocket;
    DataOutputStream socketOutput;
    DataInputStream fromClient;
    boolean continuarRodando = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_player);

        getRandomCEP();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(WaitingPlayer.this, Game.class);
        intent.putExtra("cep-digits", this.cepDigits);
        intent.putExtra("full-cep", this.cep);
        startActivity(intent);
//        ligarServidor();
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

                this.cep = Integer.parseInt(cep.split("-")[0] + cep.split("-")[1]);
                this.cepDigits = Integer.parseInt(cep.split("-")[1]);

                Log.v("[DEBUG]", "CEP = " + cep
                        + "\nCEP int = " + this.cep
                        + "\nCEP digit = " + this.cepDigits
                        + "\nLOGRADOURO = " + logradouro
                        + "\nBAIRO = " + bairro
                        + "\nCIDADE = " + cidade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ligarServidor() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ligarServerCodigo();
            }
        });
        t.start();
    }

    public void ligarServerCodigo() {
        String result = "";
        try {
            welcomeSocket = new ServerSocket(9090);
            Socket connectionSocket = welcomeSocket.accept();

            Log.v("PDM", "Device connected");

            fromClient = new DataInputStream(connectionSocket.getInputStream());
            socketOutput = new DataOutputStream(connectionSocket.getOutputStream());

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(WaitingPlayer.this, Game.class);
            intent.putExtra("cep-digits", this.cepDigits);
            intent.putExtra("full-cep", this.cep);
            startActivity(intent);
            finish();

//            continuarRodando = true;
//            while (continuarRodando) {
//                result = fromClient.readUTF();
//                if (result.compareTo("PING") == 0) {
//                    socketOutput.writeUTF("PONG");
//                    socketOutput.flush();
//                    atualizarStatus();
//                }
//            }
//
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void atualizarStatus() {
//        tvNumPìngsPongs.post(new Runnable() {
//            @Override
//            public void run() {
//                tvNumPìngsPongs.setText("Enviados Pings e Pongs");
//            }
//        });
//    }

//    public void mandarPing() {
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (socketOutput != null) {
//                        socketOutput.writeUTF("PING");
//                        socketOutput.flush();
//                        atualizarStatus();
//                    } else {
//                        tvStatus.setText("Cliente Desconectado");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        t.start();
//    }
}
