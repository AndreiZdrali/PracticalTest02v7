package ro.pub.cs.systems.eim.practicaltest02v7;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PracticalTest02v7MainActivity extends AppCompatActivity {

    private ServerThread serverThread = null;
    private EditText serverPortEditText, adresaServerEditText, clientPortEditText;
    private EditText actiuneEditText, minutEditText, secundaEditText;
    private Button startServerButton, executeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v7_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        adresaServerEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        actiuneEditText = findViewById(R.id.action_edit_text);
        minutEditText = findViewById(R.id.minut_edit_text);
        secundaEditText = findViewById(R.id.secunda_edit_text);
        startServerButton = findViewById(R.id.connect_button);
        executeButton = findViewById(R.id.execute_button);

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String portStr = serverPortEditText.getText().toString();
                int port = 2000;
                if (!portStr.isEmpty()) {
                    try {
                        port = Integer.parseInt(portStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                serverThread = new ServerThread(port);
                serverThread.start();
                Log.d("MainThread", "Started Server on port " + portStr);
            }
        });

        executeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server = adresaServerEditText.getText().toString();
                String port = clientPortEditText.getText().toString();
                String action = actiuneEditText.getText().toString();
                String minute = minutEditText.getText().toString();
                String second = secundaEditText.getText().toString();
                if (!action.isEmpty()) {
                    new ClientThread(server.trim(), Integer.parseInt(port.trim()),
                            action.trim(), minute.trim(), second.trim()).start();
                }
            }
        });

    }
}