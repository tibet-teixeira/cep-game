package tesky.cep.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tesky.cep.R;

public class MainActivity extends AppCompatActivity {
    Button createRoomButton;
    Button loginRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createRoomButton = findViewById(R.id.createRoomButton);
        loginRoomButton = findViewById(R.id.loginRoomButton);

        createRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        loginRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, LoginRoomActivity.class);
                Intent intent = new Intent(MainActivity.this, WaitingPlayer.class);
                startActivity(intent);
            }
        });
    }
}
