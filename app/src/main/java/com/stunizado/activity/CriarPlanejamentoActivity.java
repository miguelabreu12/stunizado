package com.stunizado.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CriarPlanejamentoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_planejamento);

        // obtenção do ViewModel
        com.stunizado.activity.AddProductViewModel addProductViewModel = new ViewModelProvider(this).get(com.stunizado.activity.AddProductViewModel.class);


        // Quando o usuário clicar no botão adicionar...
        Button btnAddProduct = findViewById(R.id.btnCriar);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Aqui nós desabilitamos o botão adicionar. Fazemos isso para impedir o usuário de
                // apertar esse botão várias vezes e, assim, cadastrar o mesmo produto de forma
                // repetida.
                v.setEnabled(false);

                // Abaixo, verificamos se o usuário preencheu todos os campos necessários. Caso não
                // exibimos uma mensagem toast para o usuário indicando qual campo ele precisa
                // preencher, habilitamos novamente o botão de adicionar e retornamos.
                EditText etnomeplanejamento = findViewById(R.id.etnomeplanejamento);
                String nome = etnomeplanejamento.getText().toString();
                if(nome.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo Nome do Produto não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etnovoplanejamento = findViewById(R.id.etnovoplanejamento);
                String novoplanejamento = etnovoplanejamento.getText().toString();
                if(novoplanejamento.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo Preço do Produto não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etdescricaoplanejamento = findViewById(R.id.etdescricaoplanejamento);
                String descricao = etdescricaoplanejamento.getText().toString();
                if(descricao.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo Descrição do Produto não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etdatainicio = findViewById(R.id.etdatainicio);
                String datainicio = etdatainicio.getText().toString();
                if(datainicio.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo Descrição do Produto não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }

                EditText etdataprova = findViewById(R.id.etdataprova);
                String dataprova = etdataprova.getText().toString();
                if(dataprova.isEmpty()) {
                    Toast.makeText(AddProductActivity.this, "O campo Descrição do Produto não foi preenchido", Toast.LENGTH_LONG).show();
                    v.setEnabled(true);
                    return;
                }


                // Neste ponto, já verificamos que todos os campos foram preenchidos corretamente.
                // Antes enviar esses dados ao servidor, nós fazemos uma escala na imagem escolhida
                // para o produto. Fazemos isso porque a câmera do celular produz imagens muito grandes,
                // com resolução muito mais alta do que aquela que realmente precisamos. Logo, na
                // prática, o que fazemos aqui é diminuir o tamanho da imagem antes de enviá-la ao
                // servidor. Isso garante que será usado menos recurso de rede e de banco de dados
                // no servidor.
                //
                // A imagem é escalada de forma que sua altura fique em 300dp (tamanho do ImageView
                // que exibe os detalhes de um produto. A largura vai possuir
                // um tamanho proporcional ao tamamnho original.
                try {
                    int h = (int) getResources().getDimension(R.dimen.img_height);
                    com.stunizado.produtos.util.Util.scaleImage(currentPhotoPath, -1, 2*h);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                // O ViewModel possui o método addProduct, que envia os dados do novo produto para o
                // servidor web.O servidor web recebe esses dados e cadastra um novo produto. Se o
                // produto foi cadastrado com sucesso, a app recebe o valor true. Se não o servidor
                // retorna o valor false.
                //
                // O método de addProduct retorna um LiveData, que na prática é um container que avisa
                // quando o resultado do servidor chegou.
                LiveData<Boolean> resultLD = addProductViewModel.addProduct(nome,novoplanejamento,descricao,datainicio,dataprova);

                // Aqui nós observamos o LiveData. Quando o servidor responder, o resultado indicando
                // se o cadastro do produto deu certo ou não será guardado dentro do LiveData. Neste momento o
                // LiveData avisa que o resultado chegou chamando o método onChanged abaixo.
                resultLD.observe(AddProductActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        // aBoolean contém o resultado do cadastro do produto. Se aBoolean for true, significa
                        // que o cadastro do produto foi feito corretamente. Indicamos isso ao usuário
                        // através de uma mensagem do tipo toast e finalizamos a Activity. Quando
                        // finalizamos a Activity, voltamos para a tela home, que mostra a lista de
                        // produtos.
                        if(aBoolean == true) {
                            Toast.makeText(AddProductActivity.this, "Produto adicionado com sucesso", Toast.LENGTH_LONG).show();
                            // indica que a Activity terminou com resultado positivo e a finaliza
                            setResult(RESULT_OK);
                            finish();
                        }
                        else {
                            // Se o cadastro não deu certo, apenas continuamos na tela de cadastro e
                            // indicamos com uma mensagem ao usuário que o cadastro não deu certo.
                            // Reabilitamos também o botão de adicionar, para permitir que o usuário
                            // tente realizar uma nova adição de produto.
                            v.setEnabled(true);
                            Toast.makeText(AddProductActivity.this, "Ocorreu um erro ao adicionar o produto", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });

}