package tesky.cep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import tesky.cep.R;


public class LoginRoomActivity extends AppCompatActivity {
    String cep = null;
    String character = null;
    String publicPlace;
    String city;
    String state;
    Socket clientSocket;
    DataInputStream socketInput;
    DataOutputStream socketOutput;

    Button buttonLoginRoom;
    EditText editTextCodeRoom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_room);

        buttonLoginRoom = findViewById(R.id.buttonLoginRoom);
        editTextCodeRoom = findViewById(R.id.editTextCodeRoom);

        buttonLoginRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect();
            }
        });
    }

    public void connect() {
        final String ip = editTextCodeRoom.getText().toString();
        Log.v("ConnectClient", "IP = " + ip);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(ip, 9090);

                    socketOutput = new DataOutputStream(clientSocket.getOutputStream());
                    socketInput = new DataInputStream(clientSocket.getInputStream());

                    while (socketInput != null) {
                        String result = socketInput.readUTF();
                        if (result.contains("CEP")) {
                            cep = result.split(":")[1];
                            Log.v("ConnectClient", "CEP = " + cep);
                        }

                        if (result.contains("CHARACTER")) {
                            character = result.split(":")[1];
                            Log.v("ConnectClient", "CHARACTER = " + character);
                        }

                        if (result.contains("PUBLIC_PLACE")) {
                            publicPlace = result.split(":")[1];
                        }

                        if (result.contains("CITY")) {
                            city = result.split(":")[1];
                        }

                        if (result.contains("STATE")) {
                            state = result.split(":")[1];
                        }

                        if ((cep != null) && (character != null)) {
//                            shutdownClient();
                            Intent intent = new Intent(LoginRoomActivity.this, Game.class);
                            intent.putExtra("cep", cep);
                            intent.putExtra("ip", ip);
                            intent.putExtra("character", character);
                            intent.putExtra("type", "client");
                            intent.putExtra("publicPlace", publicPlace);
                            intent.putExtra("city", city);
                            intent.putExtra("state", state);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void shutdownClient() {
        try {
            if (socketOutput != null) {
                socketOutput.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}