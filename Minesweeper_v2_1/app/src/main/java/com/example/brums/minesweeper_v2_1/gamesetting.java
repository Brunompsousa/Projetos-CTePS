package com.example.brums.minesweeper_v2_1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class gamesetting extends Activity {

    //******************************//
    //********* Variaveis **********//
    //******************************//

    Button Start;
    EditText linhas;
    EditText colunas;
    EditText bombas;
    boolean verifycl = false;
    boolean verifyb = false;

    int jogadores = 0;

    //******************************//
    //********** OnCreate **********//
    //******************************//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamesetting);

            jogadores = getIntent().getExtras().getInt("players");

        // Ligacao para comecar o jogo no click do botao
        Start = (Button) findViewById(R.id.goforgame);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int lins=0,cols=0,bombs=0;

                Intent intent = new Intent(view.getContext(), MainActivity.class);

                linhas = findViewById(R.id.getLinhas);
                if(linhas.getText().toString().trim().length() > 0) {
                    lins = Integer.parseInt(linhas.getText().toString());

                    if (lins >= 8 && lins <= 15)
                        verifycl = true;
                }

                colunas = findViewById(R.id.getColunas);
                if(colunas.getText().toString().trim().length() > 0) {
                    cols = Integer.parseInt(colunas.getText().toString());

                    if (cols >= 8 && cols <= 15)
                        verifycl = true;
                }

                bombas = findViewById(R.id.getBombas);
                if (bombas.getText().toString().trim().length() > 0) {
                    bombs = Integer.parseInt(bombas.getText().toString());

                    if (bombs >= 10 && bombs <= 40)
                        verifyb = true;
                }

                if (verifycl && verifyb) {

                    intent.putExtra("linhas", lins);
                    intent.putExtra("colunas", cols);
                    intent.putExtra("bombas", bombs);
                    intent.putExtra("jogadores", jogadores);

                    startActivity(intent);// for calling the activity
                    finish();
                }
                else {
                    if (!verifycl && verifyb)
                        showDialog(getString(R.string.errorLinCol), 5000);
                    if (!verifyb && verifycl)
                    showDialog(getString(R.string.errobombas), 5000);
                    if (!verifyb && !verifycl)
                        showDialog(getString(R.string.Errorall), 5000);
                }
            }
        });
    }


    //******************************//
    /*
    *   Funcao para mostrar mensagem no ecran
    */
    //******************************//
    private void showDialog(String message, int milliseconds)
    {
        // show message
        Toast dialog = Toast.makeText(
                getApplicationContext(),
                message,
                Toast.LENGTH_LONG);

        dialog.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout dialogView = (LinearLayout) dialog.getView();

        dialog.setDuration(milliseconds);
        dialog.show();
    }

}
