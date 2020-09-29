package tesky.cep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import tesky.cep.R;
import tesky.cep.model.SocketHandler;

public class Game extends AppCompatActivity {
    static int attemps = 0;

    int cep;
    int cepDigits;
    String cepString;
    String character;
    String opponentCharacter;
    Socket socket;
    SocketHandler socketHandler;
    DataInputStream socketInput;
    DataOutputStream socketOutput;

    TextView playerTextView;
    TextView cepTextView;
    EditText digitsCepEditText;
    TextView publicPlaceTextView;
    TextView cityTextView;
    TextView statusTextView;
    TextView pointsTextView;
    TextView attempsTextView;
    Button playButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        Intent intent = this.getIntent();
        Bundle params = intent.getExtras();
        if (params != null) {
            cepString = params.getString("cep");
            character = params.getString("character");
            socketHandler = (SocketHandler) params.getSerializable("socket");
        }

        if (character.equals("Morlock")) opponentCharacter = "Eloi";
        else opponentCharacter = "Morlock";

        try {
            socket = socketHandler.getSocket();
            socketInput = new DataInputStream(socket.getInputStream());
            socketOutput = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        cepDigits = Integer.parseInt(cepString.split("-")[1]);
        cepDigits = Integer.parseInt(cepString.split("-")[0] + cepString.split("-")[1]);

        playerTextView = findViewById(R.id.playerTextView);
        cepTextView = findViewById(R.id.cepTextView);
        digitsCepEditText = findViewById(R.id.digitsCepEditText);
        publicPlaceTextView = findViewById(R.id.publicPlaceTextView);
        cityTextView = findViewById(R.id.cityTextView);
        statusTextView = findViewById(R.id.statusTextView);
        pointsTextView = findViewById(R.id.pointsTextView);
        attempsTextView = findViewById(R.id.attempsTextView);
        playButton = findViewById(R.id.playButton);

        cepTextView.setText("CEP: " + cepString.split("-")[0] + "-");
        publicPlaceTextView.setText("Personagem: " + character);

        play();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setEnabled(false);
                attemps += 1;
                int digits = Integer.parseInt(digitsCepEditText.getText().toString());

                Log.v("[DEBUG]", "CEP = " + cepDigits
                        + "\nDIGITS " + digits);

                if (digits > cepDigits) {
                    statusTextView.setText("MAIOR");
                } else if (digits < cepDigits) {
                    statusTextView.setText("MENOR");
                } else {
                    statusTextView.setText("IGUAL");
                }

                playerTextView.setText("Jogada do " + opponentCharacter);
                attempsTextView.setText("Tentativas: " + attemps);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (socketOutput != null) {
                                socketOutput.writeUTF("SuaVez");
                                socketOutput.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                t.start();
            }
        });
    }

    public void play() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (socketInput != null) {
                        String result = socketInput.readUTF();
                        if (result.compareTo("SuaVez") == 0) {
                            playButton.post(new Runnable() {
                                @Override
                                public void run() {
                                    playButton.setEnabled(true);
                                }
                            });

                            playerTextView.post(new Runnable() {
                                @Override
                                public void run() {
                                    playerTextView.setText("Rodada de " + character);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
