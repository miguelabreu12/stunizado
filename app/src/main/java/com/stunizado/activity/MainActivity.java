package com.stunizado.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.stunizado.util.Config;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se o usuário ainda não logou, então não existe informação de login guardada na app.
        // Então a app é redirecionada para a tela de login.
        if(Config.getLogin(MainActivity.this).isEmpty()) {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        // Se o usuário já logou, então a informação de login está guardada na app. Então
        // a app é redirecionada para a tela principal da app (HomeActivity)
        else {
            Intent i = new Intent(MainActivity.this, HomePlanejamentoActivity.class);
            startActivity(i);
            finish();
        }
    }
}