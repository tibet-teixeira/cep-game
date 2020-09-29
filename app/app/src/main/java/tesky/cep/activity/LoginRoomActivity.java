package tesky.cep.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import tesky.cep.R;


public class LoginRoomActivity extends AppCompatActivity {
    TextView tvStatus, tvNumPìngsPongs;
    Socket clientSocket;
    DataOutputStream socketOutput;
    BufferedReader socketEntrada;
    DataInputStream socketInput;
    Button btConectar;
    Button btPing;
    EditText edtIp;
    long pings, pongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_room);
        tvStatus = findViewById(R.id.tvStatusClient);
        btConectar = findViewById(R.id.btConectar);
        btPing = findViewById(R.id.btPingC);
        tvNumPìngsPongs = findViewById(R.id.tvNumPP_C);
        edtIp = findViewById(R.id.edtIP);


        btPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandarPing();
            }
        });

        btConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conectar();
            }
        });
    }

    public void atualizarStatus() {
        //Método que vai atualizar os pings e pongs, usando post para evitar problemas com as threads
        tvNumPìngsPongs.post(new Runnable() {
            @Override
            public void run() {
                tvNumPìngsPongs.setText("Enviados " + pings + " Pings e " + pongs + " Pongs");
            }
        });
    }

    public void conectar() {
        final String ip = edtIp.getText().toString();
        tvStatus.setText("Conectando em " + ip + ":9090");


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip, 9090);

                    tvStatus.post(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText("Conectado com " + ip + ":9090");
                        }
                    });
                    socketOutput =
                            new DataOutputStream(clientSocket.getOutputStream());
                    socketInput =
                            new DataInputStream(clientSocket.getInputStream());
                    while (socketInput != null) {
                        String result = socketInput.readUTF();
                        if (result.compareTo("PING") == 0) {
                            //enviar Pong
                            pongs++;
                            socketOutput.writeUTF("PONG");
                            socketOutput.flush();
                            atualizarStatus();
                        }
                    }


                } catch (Exception e) {

                    tvStatus.post(new Runnable() {
                        @Override
                        public void run() {
                            tvStatus.setText("Erro na conexão com " + ip + ":9090");
                        }
                    });

                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void mandarPing() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (socketOutput != null) {
                        socketOutput.writeUTF("PING");
                        socketOutput.flush();
                        pings++;
                        atualizarStatus();
                    } else {
                        tvStatus.setText("Cliente Desconectado");
                        btConectar.setEnabled(true);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}