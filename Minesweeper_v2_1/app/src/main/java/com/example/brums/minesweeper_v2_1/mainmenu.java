package com.example.brums.minesweeper_v2_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


/**
 * Created by Brums on 27-01-2018.
 */

public class mainmenu extends Activity {

    //******************************//
    //********* Variaveis **********//
    //******************************//

    Button btn1jog;
    Button btn2jog;
    Button btncreditos;


    //******************************//
    //********** OnCreate **********//
    //******************************//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        // Ligacao para o 1jogador para comecar o jogo
        btn1jog = (Button) findViewById(R.id.JogadorUm);
        btn1jog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), gamesetting.class);
                intent.putExtra("players",1);
                startActivity(intent);// for calling the activity

            }
        });

        // Ligacao para o 1jogador para comecar o jogo
        btn2jog = (Button) findViewById(R.id.JogadorDois);
        btn2jog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), gamesetting.class);
                intent.putExtra("players",2);
                startActivity(intent);// for calling the activity

            }
        });

        // Ligacao para o 1jogador para comecar o jogo
        btncreditos = (Button) findViewById(R.id.Creditos);
        btncreditos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), gamecreditos.class);
                startActivity(intent);// for calling the activity
                finish();
            }
        });

    }

}
