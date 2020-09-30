package tesky.cep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tesky.cep.R;

public class EndGameActivity extends AppCompatActivity {
    String playerStatus;
    String playerCharacter;
    String publicPlace;
    String city;
    String state;

    TextView playerStatusTextView;
    TextView playerCharacterTextView;
    TextView publicPlaceTextView;
    TextView cityTextView;
    TextView stateTextView;
    Button returnHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_game);

        Intent intent = this.getIntent();
        Bundle params = intent.getExtras();
        if (params != null) {
            playerStatus = params.getString("status");
            playerCharacter = params.getString("character");
            publicPlace = params.getString("publicPlace");
            city = params.getString("city");
            state = params.getString("state");
        }

        playerStatusTextView = findViewById(R.id.playerStatusTextView);
        playerCharacterTextView = findViewById(R.id.playerCharacterTextView);
        publicPlaceTextView = findViewById(R.id.publicPlaceTextView);
        cityTextView = findViewById(R.id.cityTextView);
        stateTextView = findViewById(R.id.stateTextView);
        returnHomeButton = findViewById(R.id.returnHomeButton);

        if (playerStatus.equals("winner")) playerStatusTextView.setText("Você ganhou");
        else playerStatusTextView.setText("Você perdeu");
        playerCharacterTextView.setText("Parabéns" + playerCharacter);
        publicPlaceTextView.setText("Logradouro" + publicPlace);
        cityTextView.setText("Cidade" + city);
        stateTextView.setText("Estado" +state);

        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
