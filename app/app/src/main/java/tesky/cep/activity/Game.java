package tesky.cep.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import tesky.cep.R;

public class Game extends AppCompatActivity {
    int cep;
    int cepDigits;

    TextView playTextView;
    TextView cepTextView;
    EditText digitsCepEditText;
    TextView public_placeTextView;
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
            cep = params.getInt("full-cep");
            cepDigits = params.getInt("cep-digits");
        }

        playTextView = findViewById(R.id.playTextView);
        cepTextView = findViewById(R.id.cepTextView);
        digitsCepEditText = findViewById(R.id.digitsCepEditText);
        public_placeTextView = findViewById(R.id.public_placeTextView);
        cityTextView = findViewById(R.id.cityTextView);
        statusTextView = findViewById(R.id.statusTextView);
        pointsTextView = findViewById(R.id.pointsTextView);
        attempsTextView = findViewById(R.id.attempsTextView);
        playButton = findViewById(R.id.playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int digits =  Integer.parseInt(digitsCepEditText.getText().toString());

                Log.v("[DEBUG]", "CEP = " + cepDigits
                        + "\nDIGITS " + digits);

               if (digits > cepDigits) {
                   statusTextView.setText("MAIOR");
               } else if (digits < cepDigits) {
                   statusTextView.setText("MENOR");
               } else {
                   statusTextView.setText("IGUAL");
               }
            }
        });
    }
}
