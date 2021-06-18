package com.example.brums.minesweeper_v2_1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class gamecreditos extends Activity {

    //******************************//
    //********* Variaveis **********//
    //******************************//

    Button btnback;


    //******************************//
    //********** OnCreate **********//
    //******************************//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamecreditos);

        // Ligacao para o 1jogador para comecar o jogo
        btnback = (Button) findViewById(R.id.Creditos);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), mainmenu.class);
                startActivity(intent);// for calling the activity
                finish();

            }
        });

    }

}
