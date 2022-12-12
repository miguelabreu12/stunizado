package com.stunizado.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.stunizado.R;
import com.stunizado.model.LoginViewModel;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    static int RESULT_REQUEST_PERMISSION = 2;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // A tela de login na prática é a primeira que vai aparecer na app. Então é aqui que a gente
        // pede as permissões necessárias. A app precisa de duas permissões:
        // - Camera -> para tirar foto do produto
        // - Acesso à galeria publica - para escolher um foto da galeria do celular como foto do
        // produto.
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        checkForPermissions(permissions);

        // A função que entra em contato com o servidor web está definida dentro da ViewModel
        // referente a essa Activity
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Quando o usuário clicar no botão de login
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Primeiro obtemos os dados de login e senha digitados pelo usuário
                EditText etEmail = findViewById(R.id.etEmail);
                final String email = etEmail.getText().toString();

                EditText etSenha = findViewById(R.id.etSenha);
                final String senha = etSenha.getText().toString();

                // O ViewModel possui o método login, que envia as informações para o servidor web.
                // O servidor web recebe as infos e verifica se estão corretas. Se sim, siginifca
                // que o login foi realizado com sucesso e a app recebe o valor true. Se as infos
                // estão incorretas, o servidor retorna o valor false.
                //
                // O método de login retorna um LiveData, que na prática é um container que avisa
                // quando o resultado do servidor chegou.
                LiveData<Boolean> resultLD = loginViewModel.login(email,senha);

                // Aqui nós observamos o LiveData. Quando o servidor responder, o resultado indicando
                // se o login deu certo ou não será guardado dentro do LiveData. Neste momento o
                // LiveData avisa que o resultado chegou chamando o método onChanged abaixo.
                resultLD.observe(LoginActivity.this, new Observer<Boolean>() {

                    // Ao ser chamado, o método onChanged informa também qual foi o resultado
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        // aBoolean contém o resultado do login. Se aBoolean for true, significa
                        // que as infos de login e senha enviadas ao servidor estão certas. Neste
                        // caso, guardamos as infos de login e senha dentro da app através da classe
                        // Config. Essas infos de login e senha precisam ser guardadas dentro da app
                        // para que possam ser usadas quando a app pedir dados ao servidor web que só
                        // podem ser obtidos se o usuário enviar o login e senha.
                        if(aBoolean) {

                            // guarda os dados de login e senha dentro da app
                            com.stunizado.util.Config.setLogin(LoginActivity.this, email);
                            com.stunizado.util.Config.setPassword(LoginActivity.this, senha);

                            // exibe uma mensagem indicando que o login deu certo
                            Toast.makeText(LoginActivity.this, "Login realizado com sucesso", Toast.LENGTH_LONG).show();

                            // Navega para tela principal
                            Intent i = new Intent(LoginActivity.this, HomePlanejamentoActivity.class);
                            startActivity(i);
                        }
                        else {

                            // Se o login não deu certo, apenas continuamos na tela de login e
                            // indicamos com uma mensagem ao usuário que o login não deu certo.
                            Toast.makeText(LoginActivity.this, "Não foi possível realizar o login da aplicação", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // Se o usuário ainda não tem login, então ele pode ir para a tela de cadastro e criar um
        // novo usuário
        Button btnRegisterNewUser = findViewById(R.id.btnLoginCadastro);
        btnRegisterNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
                startActivity(i);
            }
        });
    }


    /**
     * Verifica se as permissões necessárias já foram concedidas. Caso contrário, o usuário recebe
     * uma janela pedindo para conceder as permissões
     * @param permissions lista de permissões que se quer verificar
     */
    private void checkForPermissions(List<String> permissions) {
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissions) {
            if( !hasPermission(permission)) {
                permissionsNotGranted.add(permission);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(permissionsNotGranted.size() > 0) {
                requestPermissions(permissionsNotGranted.toArray(new String[permissionsNotGranted.size()]),RESULT_REQUEST_PERMISSION);
            }
        }
    }

    /**
     * Verifica se uma permissão já foi concedida
     * @param permission
     * @return true caso sim, false caso não.
     */
    private boolean hasPermission(String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(LoginActivity.this, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * Método chamado depois que o usuário já escolheu as permissões que quer conceder. Esse método
     * indica o resultado das escolhas do usuário.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        final List<String> permissionsRejected = new ArrayList<>();
        if(requestCode == RESULT_REQUEST_PERMISSION) {

            for(String permission : permissions) {
                if(!hasPermission(permission)) {
                    permissionsRejected.add(permission);
                }
            }
        }

        if(permissionsRejected.size() > 0) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    new AlertDialog.Builder(LoginActivity.this).
                            setMessage("Para usar essa app é preciso conceder essas permissões").
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), RESULT_REQUEST_PERMISSION);
                                }
                            }).create().show();
                }
            }
        }
    }
}