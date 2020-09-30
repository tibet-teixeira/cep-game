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
import java.net.ServerSocket;
import java.net.Socket;

import tesky.cep.R;

public class Game extends AppCompatActivity {
    static int attemps = 0;
    static boolean winner = false;
    int cepDigits;
    String cepString;
    String character;
    String opponentCharacter;
    String ipAddress;
    String type;
    Socket socket;
    String publicPlace;
    String city;
    String state;
    ServerSocket serverSocket;
    DataInputStream socketInputServer;
    DataOutputStream socketOutputServer;
    DataInputStream socketInputClient;
    DataOutputStream socketOutputClient;

    TextView playerTextView;
    TextView cepTextView;
    EditText digitsCepEditText;
    TextView statusTextView;
    TextView characterTextView;
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
            ipAddress = params.getString("ip");
            type = params.getString("type");
            publicPlace = params.getString("publicPlace");
            city = params.getString("city");
            state = params.getString("state");
        }

        if (character.equals("Morlock")) opponentCharacter = "Eloi";
        else opponentCharacter = "Morlock";

        cepDigits = Integer.parseInt(cepString.split("-")[1]);

        playerTextView = findViewById(R.id.playerTextView);
        cepTextView = findViewById(R.id.cepTextView);
        digitsCepEditText = findViewById(R.id.digitsCepEditText);
        statusTextView = findViewById(R.id.statusTextView);
        characterTextView = findViewById(R.id.characterTextView);
        attempsTextView = findViewById(R.id.attempsTextView);
        playButton = findViewById(R.id.playButton);

        cepTextView.setText("CEP: " + cepString.split("-")[0] + "-");
        characterTextView.setText("Personagem: " + character);

        if (type.equals("server")) {
            playerTextView.setText("Rodada de " + this.character);
            connectServer();
        } else {
            playerTextView.setText("Rodada de " + this.opponentCharacter);
            playButton.setEnabled(false);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            connectClient();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setEnabled(false);
                attemps += 1;
                int digits = Integer.parseInt(digitsCepEditText.getText().toString());

                Log.v("[DEBUG]", "CEP = " + cepDigits
                        + "\nDIGITS " + digits);

                if (digits > cepDigits) {
                    statusTextView.setText("Status: MAIOR");
                } else if (digits < cepDigits) {
                    statusTextView.setText("Status: MENOR");
                } else {
                    statusTextView.setText("Status: IGUAL");
                    winner = true;
                }

                playerTextView.setText("Rodada de " + opponentCharacter);
                attempsTextView.setText("Tentativas: " + attemps);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (type.equals("server")) {
                                if (socketOutputServer != null) {
                                    if (winner) {
                                        socketOutputServer.writeUTF("ServerWinner");
                                        Intent intent = new Intent(Game.this, EndGameActivity.class);
                                        intent.putExtra("status", "winner");
                                        intent.putExtra("character", character);
                                        intent.putExtra("publicPlace", publicPlace);
                                        intent.putExtra("city", city);
                                        intent.putExtra("state", state);
                                        startActivity(intent);
                                    } else {
                                        socketOutputServer.writeUTF("ClientTurn");
                                    }
                                    socketOutputServer.flush();
                                }
                            } else {
                                if (socketOutputClient != null) {
                                    if (winner) {
                                        socketOutputClient.writeUTF("ClientWinner");
                                        Intent intent = new Intent(Game.this, EndGameActivity.class);
                                        intent.putExtra("status", "winner");
                                        intent.putExtra("character", character);
                                        intent.putExtra("publicPlace", publicPlace);
                                        intent.putExtra("city", city);
                                        intent.putExtra("state", state);
                                        startActivity(intent);
                                    } else {
                                        socketOutputClient.writeUTF("ServerTurn");
                                    }
                                    socketOutputClient.flush();
                                }
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

    public void connectServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(9091);
                    Socket connectionSocket = serverSocket.accept();

                    socketInputServer = new DataInputStream(connectionSocket.getInputStream());
                    socketOutputServer = new DataOutputStream(connectionSocket.getOutputStream());

                    try {
                        socketOutputServer.writeUTF("ConnectServerOK");
                        socketOutputServer.flush();

                        while (socketInputServer != null) {
                            String result = socketInputServer.readUTF();
                            if (result.compareTo("ServerTurn") == 0) {
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
                            if (result.compareTo("ClientWinner") == 0) {
                                Intent intent = new Intent(Game.this, EndGameActivity.class);
                                intent.putExtra("status", "loser");
                                intent.putExtra("character", opponentCharacter);
                                intent.putExtra("publicPlace", publicPlace);
                                intent.putExtra("city", city);
                                intent.putExtra("state", state);
                                startActivity(intent);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void connectClient() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ipAddress, 9091);

                    socketOutputClient = new DataOutputStream(socket.getOutputStream());
                    socketInputClient = new DataInputStream(socket.getInputStream());

                    while (socketInputClient != null) {
                        String result = socketInputClient.readUTF();
                        if (result.compareTo("ClientTurn") == 0) {
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
                        if (result.compareTo("ServerWinner") == 0) {
                            Intent intent = new Intent(Game.this, EndGameActivity.class);
                            intent.putExtra("status", "loser");
                            intent.putExtra("character", opponentCharacter);
                            intent.putExtra("publicPlace", publicPlace);
                            intent.putExtra("city", city);
                            intent.putExtra("state", state);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
