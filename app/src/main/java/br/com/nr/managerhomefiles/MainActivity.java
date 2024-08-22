package br.com.nr.managerhomefiles;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



public class MainActivity extends AppCompatActivity {

    private Uri URLFile ;
    private String categoryFile;
    private TextView textStatus;
    private TextView textCategory;
    private Button buttonSave;
    public String[] opcoes = {"Energia", "Agua","LojaRoupa","Saude"};

    private Spinner menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textStatus = findViewById(R.id.tx_status_documento);
        buttonSave = findViewById(R.id.btn_save_file);
        textCategory = findViewById(R.id.tx_category);
        menu = findViewById(R.id.spinnerTipo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, opcoes);
        menu.setAdapter(adapter);
        menu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textCategory.setText(opcoes[i]);
                categoryFile = opcoes[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePdfLocally(URLFile);
            }
        });


       // Log.d("TAG", "Mensagem de Verbose");
       // Toast.makeText(this, "message", Toast.LENGTH_LONG).show();

         handleIncomingIntent(getIntent()); // Verifica e processa o Intent inicial
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent); // Verifica e processa o Intent quando a atividade já está em execução
    }

    private void handleIncomingIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
          //
            handleSendIntent(intent); // Processar um único arquivo
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            Toast.makeText(this, "miitos", Toast.LENGTH_LONG).show();
            handleSendMultipleIntent(intent); // Processar múltiplos arquivos
        }
    }

    private void handleSendIntent(Intent intent) {
        String TAG = "ListaProdutosActivity";


        Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (fileUri != null) {
            // Manipular o arquivo aqui
            // Por exemplo, exibir a URI ou abrir o arquivo
            //Log.d("MainActivity", "Arquivo recebido: " + fileUri.toString());
            Log.i(TAG, fileUri.toString());
            Toast.makeText(this, "Arquivo Pronto",Toast.LENGTH_LONG).show();
            URLFile = fileUri;
            textStatus.setText("Arquivo em Aguardo!");


        }
        else{
            Log.i(TAG, "sem info");
            Toast.makeText(this, "", Toast.LENGTH_LONG).show();
        }
    }

    private void savePdfLocally(Uri fileUri) {
        String TAG = "ListaProdutosActivity";

        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(fileUri);
            if (inputStream != null) {
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                // Crie uma nova pasta dentro de "Downloads"
                File customDir = new File(downloadsDir, "MyCustomFolder");
                if (!customDir.exists()) {
                    customDir.mkdirs(); // Cria o diretório se ele não existir
                }
                Log.i(TAG, "customDir: " + customDir);

                File pdfFile = new File(customDir, categoryFile+".pdf");

                FileOutputStream outputStream = new FileOutputStream(pdfFile);
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();

                Log.i(TAG, "PDF salvo em: " + pdfFile.getAbsolutePath());
                Toast.makeText(this, "Arquivo Salvo",Toast.LENGTH_LONG).show();
                textStatus.setText("Arquivo em Salvo!");
            } else {
                Toast.makeText(this, "Não foi possível abrir o arquivo", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e(TAG, "Erro ao salvar o arquivo", e);
            Toast.makeText(this, "Erro ao salvar o arquivo", Toast.LENGTH_LONG).show();
        }
    }

    private void handleSendMultipleIntent(Intent intent) {
        ArrayList<Uri> fileUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (fileUris != null) {
            for (Uri fileUri : fileUris) {
                // Manipular cada arquivo aqui
                // Por exemplo, exibir a URI ou abrir o arquivo
                Log.d("MainActivity", "Arquivo recebido: " + fileUri.toString());
            }
        }
    }
}
