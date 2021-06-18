package com.example.brums.minesweeper_v2_1;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    //******************************//
    //********* Variaveis **********//
    //******************************//

    TextView txtScore;
    TextView txtTimer;
    Button btnStart;
    ImageButton changemode;

    // Array de block para depois adicionarmos ao campo
    Block blocos[][];

    //Arrays para guardar os block quando estamos a abrir os que contenham 0 bombas junto a si
    ArrayList<Integer> blockLinha = new ArrayList<Integer>();
    ArrayList<Integer> blockColuna = new ArrayList<Integer>();



    // Parametros para dar aos botoes e aos linear layouts de modo a que o espaco seja dividido por todos
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1);

    int players = 1;
    int numL = 8;
    int numC = 8;
    int MinesTotal = 10;
    int Playing = 1;

    // Handler para manter o tempo atualizado
    Handler timer = new Handler();
    int secondsPassed = 0;

    boolean isTimerStarted; // Verifica se o tempo ja comecou ou nao a contar
    boolean areMinesSet; // Verifica se já foram inseridas as bombas no campo de jogo
    boolean isGameOver; //Verifica se perdemos o jogo ou se acabou
    int minesToFind; // Numero de minas ainda por descobrir


    //******************************//
    //********** OnCreate **********//
    //******************************//

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        players = getIntent().getExtras().getInt("jogadores");
        numL = getIntent().getExtras().getInt("linhas");
        numC = getIntent().getExtras().getInt("colunas");
        MinesTotal = getIntent().getExtras().getInt("bombas");


        // Ligacao para o Linearlayout que contem o campo de jogo
        final LinearLayout tableLAY = findViewById(R.id.MSfield);

        // Ligacao para o textview do contador de minas
        txtScore = (TextView) findViewById(R.id.MineCount);

        // Ligacao para o textview do contador de tempo
        txtTimer = (TextView) findViewById(R.id.Timer);

        // Ligacao para o botao para comecar o jogo
        btnStart = (Button) findViewById(R.id.Start);

        // Quando o botao para iniciar o jogo e clickado
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startNewGame();

            }
        });

        //Ligacao para o botao de modar o modo de jogo
        changemode = (ImageButton) findViewById(R.id.switchmode);

        // Quando o botao para modar o modo de jogo e clickado
        changemode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isGameOver) {
                    if (players == 2) {
                        Playing = 1;
                        players = 1;

                        showDialog("" + players + getString(R.string.oneplaymode), 3000);
                    } else {
                        players = 2;
                        Playing = 1;

                        showDialog("" + players + getString(R.string.twoplayermode), 3000);
                    }
                }


            }
        });

        showDialog(getString(R.string.textcomecar), 3000);
    }

    //******************************//
    /*
    *   Cria e mostra o campo de jogo
    *   metendo as minas que faltam encontrar como o total de minas
    *   mete a variavel que nos diz se perdemos o jogo a false
    *   e o tempo de jogo a '0'
    */
    //******************************//
    private void startNewGame() {

        // Ligacao para o Linearlayout que contem o campo de jogo
        LinearLayout tableLAY = findViewById(R.id.MSfield);

        // Ligacao para o textview do contador de tempo
        txtTimer = (TextView) findViewById(R.id.Timer);

        // Se o campo de jogo nao estiver vazio, temos de limpar o campo e fazer um novo para um novo jogo
        if (blocos != null) {

            //acaba o jogo em que estamos
            endExistingGame();

            if (players == 2)
                //Comeca novo jogo, o jogador a jogar primeiro e o 1
                Playing = 1;

            //Comeca um novo jogo
            createMineField();

        }

        else{

            if (players == 2)
                //Comeca novo jogo, o jogador a jogar primeiro e o 1
                Playing = 1;

            //Comeca um novo jogo
            createMineField();


        }

        //Mete o total de minas do jogo para a variar de minas a encontrar que dps vamos meter no textview
        minesToFind = MinesTotal;
        //Mete a variavel de verificacao se perdemos / acabou o jogo a falso
        isGameOver = false;
        //Mete a '0' a variavel dos segundos que passaram, isto e, o tempo de jogo
        secondsPassed = 0;
    }

    //******************************//
    /*
    *   Funcao para criar o campo de jogo
    */
    //******************************//
    private void createMineField()
    {

        // Ligacao para o Linearlayout que contem o campo de jogo
        LinearLayout tableLAY = findViewById(R.id.MSfield);

        // Declaramos o array de blocks (botoes) para fazer o campo de jogo
        blocos = new Block[numL][numC];

        for (int linhas = 0;linhas < numL; linhas++) {

            //Criacao da linha onde serao postos os botoes
            LinearLayout linha = new LinearLayout(this);

            for (int colunas = 0;colunas < numC; colunas++)
            {

                //Guardamos os valores das variaveis 'i' e 'j' como final, para que seja possiver usar no clicklistener
                final int i_ = linhas;
                final int j_ = colunas;

                //criamos um block e metemos no array
                blocos[linhas][colunas] = new Block(this);
                //damos ao block as definicoes default
                blocos[linhas][colunas].setDefaults();
                //metemos os parametros para que fiquei com o peso 1
                blocos[linhas][colunas].setLayoutParams(p);

                //Definicao do que acontece ao clicar no botao
                blocos[linhas][colunas].setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        //Verifica se o tempo esta a contar, se nao estiver, chama a funcao para comecar o tempo
                        if (!isTimerStarted)
                        {
                            startTimer();
                            isTimerStarted = true;
                        }

                        // Mete as minas nas casas chamando a funcao para o efeito
                        if (!areMinesSet)
                        {
                            areMinesSet = true;

                            //funcao para meter as minas no campo de jogo
                            setMines(i_, j_);
                        }

                        updateMineCountDisplay();

                        //Vai verificar se o block esta marcado com bandeira ou nao e se e clicaver
                        if (!blocos[i_][j_].getisFlagged() && blocos[i_][j_].getisClickable())
                        {

                            // Chama a funcao para abrir todos os blocos perto do clicado
                            rippleUncover(i_, j_);

                            // se clicarmos em uma bomba, chama a funcao de fim do jogo, pois perdemos
                            if (blocos[i_][j_].gethasMine())
                            {
                                // Funcao para acabar o jogo
                                finishGame(i_,j_);
                            }

                            // Funcao para verificar se ganhamos
                            if (checkGameWin())
                            {
                                // chama a funcao para dizer que ganhamos o jogo
                                winGame();
                            }

                        }

                        if (!isGameOver) {
                            if (players == 2) {
                                if (Playing == 1) {
                                    Playing = 2;
                                    showDialog(getString(R.string.playerplaing) + Playing + getString(R.string.turn), 1000);
                                } else {
                                    Playing = 1;
                                    showDialog(getString(R.string.playerplaing) + Playing + getString(R.string.turn), 1000);
                                }

                            }
                        }

                    } //Onclick

                });//ClickListener

                //******************************//*/
                /* Quando e dado um longclick em um block que contenha um numero diferente de 0 que ja nao esteja revelado
                 * verifica se o numero de bombas ao redor do block e igual ao de bandeiras colocadas
                 *
                 * Se o numero for igual, revela todos od block's a volta do qual fazemos longclick
                 * Se o numero for diferente nao faz nada
                */
                //******************************//
                blocos[i_][j_].setOnLongClickListener(new View.OnLongClickListener()
                {
                    public boolean onLongClick(View view) {

                    /*
                    * Para um LongClick o que fazemos é:
                    *
                    * Se for clicaver e estiver ativo ou com bandeira
                    * Fazemos entao uma accao dependendo do que o block tiver
                    *
                    * Se nao tiver flag nem '?' - metes como flag
                    * Se estiver Flag - metemos como '?'
                    * Se estiver '?' - Voltamos a meter normal
                    *
                    */

                    if (blocos[i_][j_].isClickable() && areMinesSet && !isGameOver && (blocos[i_][j_].getisClickable() || blocos[i_][j_].getisFlagged()))
                        {
                            // meter o block de normal para flag
                            if (!blocos[i_][j_].getisFlagged() && !blocos[i_][j_].getisQuestionMarked()) // se nao tem flag nem '?'
                            {
                                blocos[i_][j_].setBlockAsDisabled(false);
                                blocos[i_][j_].setFlagIcon(true);
                                blocos[i_][j_].setFlagged(true);
                                minesToFind--; //Reduzimos o numero de bombas
                                updateMineCountDisplay(); //Fazemos o update do contador
                            }
                            // meter o block de flag para '?'
                            else if (!blocos[i_][j_].getisQuestionMarked()) //nao tem '?' (tem flag)
                                {
                                    blocos[i_][j_].setBlockAsDisabled(false);
                                    blocos[i_][j_].setQuestionMarkIcon(true);
                                    blocos[i_][j_].setFlagged(false);
                                    blocos[i_][j_].setQuestionMarked(true);
                                    minesToFind++; // Aumentamos o numero de bombas
                                    updateMineCountDisplay(); //Fazemos o update do contador
                                }
                                // meter o block de '?' para normal
                                else //tem '?'
                                {
                                    blocos[i_][j_].setBlockAsDisabled(false);
                                    blocos[i_][j_].setQuestionMarked(false);
                                }
                        }

                        return true;

                    } //Longclick

                }); //ClickListener

            //adicionamos o botao a linha
            linha.addView(blocos[linhas][colunas]);

            }//for J

            //metemos os parametros para que fiquei com o peso 1
            linha.setLayoutParams(p);
            //adicionamos a linha ao layout destinado ao jogo
            tableLAY.addView(linha);

        }//For I

    }


    //******************************/
    /*
    *   Funcao que corre o array de modo a verificar se todos os block's sem mina ja foram revelados
    *
    *   Quando encontra um block sem mina que ainda nao tenha sido revelado, para o ciclo returnando false
    *
    */
    //******************************//
    private boolean checkGameWin()
    {
        for (int l = 0; l < numL; l++)
        {
            for (int c = 0; c < numC; c++)
            {
                if (!blocos[l][c].gethasMine() && blocos[l][c].getisCovered())
                {
                    return false;
                }
            }
        }
        return true;
    }

    //******************************/
    /*
    *   Funcao para fazer o update de quantas minas ainda falta ao utilizador marcar/descobrir
    */
    //******************************//
    private void updateMineCountText()
    {
        if (minesToFind < 0)
        {
            txtScore.setText(Integer.toString(minesToFind));
        }
        else if (minesToFind < 10)
        {
            txtScore.setText("00" + Integer.toString(minesToFind));
        }
        else if (minesToFind < 100)
        {
            txtScore.setText("0" + Integer.toString(minesToFind));
        }
        else
        {
            txtScore.setText(Integer.toString(minesToFind));
        }
    }

    private void winGame()
    {
        stopTimer();
        isTimerStarted = false;
        isGameOver = true;
        minesToFind = 0; //Mete as minas a encontrar igual a 0

        updateMineCountText(); // Faz update ao textview que mostra o numero de bombas

        // da disable a todos os botoes do campo de jogo
        // e mete bandeiras em todas as bombas que nao estavam marcadas com bandeiras
        for (int l = 0; l < numL-1; l++)
        {
            for (int c = 0; c < numC - 1; c++)
            {
                blocos[l][c].setClickable(false);
                if (blocos[l][c].gethasMine())
                {
                    blocos[l][c].setBlockAsDisabled(false);
                    blocos[l][c].setFlagIcon(true);
                }
            }
        }

        if (players == 2 )
        {
            // Mostra a mensagem
            showDialog(getString(R.string.nowinner) + Integer.toString(secondsPassed) + getString(R.string.winpart2), 1000);
        }
        else {
            // Mostra a mensagem
            showDialog(getString(R.string.winpart1) + Integer.toString(secondsPassed) + getString(R.string.winpart2), 1000);
        }
    }


    //******************************/
    /*
    *   Para o contador de tempo
    *   Define o timer e o Score para 0
    *   Apaga todas as linhas do campo de jogo
    *   e mete todas as variaveis de controlo como false
    */
    //******************************//

    private void endExistingGame()
    {

        // Ligacao para o Linearlayout que contem o campo de jogo
        LinearLayout tableLAY = findViewById(R.id.MSfield);

        stopTimer(); // Para o timer
        txtTimer.setText("000"); // Reset ao texto do Timer
        txtScore.setText("000"); // Reset ao texto do Score

        // Remove todas as linhas do campo de jogo
        tableLAY.removeAllViews();

        // Mete todas as variaveis de controlo para 'false' e a de minas a encontrar a '0'
        isTimerStarted = false;
        areMinesSet = false;
        isGameOver = false;
        minesToFind = 0;
    }

    //******************************/
    /*
    *   Esta funcao e lancada quando um jogador clica num block que contenha uma bomba
    *   Vai terminar o jogo, dar disable a todos os block do campo e parar o timer
    *   Mostra tambem uma mensagem ao jogador a dizer que perdeu e o tempo de jogo ate ao acontecimento
    */
    //******************************//
    private void finishGame(int currentRow, int currentColumn)
    {
        isGameOver = true; // Marcamos o jogo como acabado
        stopTimer(); // paramos o contador de tempo
        isTimerStarted = false; // Metemos a false o verificador para ver se o tempo esta ou nao a contar

        // Mostra todas as minas
        // e da disable a todos os block do campo de jogo
        for (int linhas = 0; linhas < numL; linhas++)
        {
            for (int column = 0; column < numC; column++)
            {
                // Faz com que o bloco seja aberto
                blocos[linhas][column].setBlockAsDisabled(true);

                // block has mine and is not flagged
                if (blocos[linhas][column].gethasMine() && !blocos[linhas][column].getisFlagged())
                {
                    // set mine icon
                    blocos[linhas][column].setMineIcon(true);
                }

                // block is flagged and doesn't not have mine
                if (!blocos[linhas][column].gethasMine() && blocos[linhas][column].getisFlagged())
                {
                    // set flag icon
                    blocos[linhas][column].setFlagIcon(true);
                }

                // block is flagged
                if (blocos[linhas][column].getisFlagged())
                {
                    // disable the block
                    blocos[linhas][column].setClickable(false);
                }
            }
        }

        // trigger mine
        blocos[currentRow][currentColumn].triggerMine();

        if (players == 1)
            // show message
            showDialog(getString(R.string.losepart1) + Integer.toString(secondsPassed) + getString(R.string.losepart2), 3000);
        else {

            if(Playing == 1)
                showDialog(getString(R.string.ponelose) + getString(R.string.ptwowin) + getString(R.string.gametime) + Integer.toString(secondsPassed) + getString(R.string.losepart2), 3000);
            else
                showDialog(getString(R.string.ptwolose) + getString(R.string.ponewin) + getString(R.string.gametime) + Integer.toString(secondsPassed) + getString(R.string.losepart2), 3000);
        }


    }



    //******************************/
    /*
    *   Funcoes que fazem o calculo dos blocos a volta das bombas
    *   dependendo de onde a bomba e colocada
    */
    //******************************//
    private void setMines(int numCol, int numLin)
    {
        // Criar um random generator para gerar os numeros para as bombas
        Random rand = new Random();

        int mineCol, mineLin;

        for (int Mines = 0; Mines < MinesTotal; Mines++)
        {
            //Gera o numero entre 0 e o numero de linhas e colunas que temos
            mineCol = rand.nextInt(numC);
            mineLin = rand.nextInt(numL);

            //verificamos se pelo menos um dos numeros nao e igual ao block que clicamos, para que nao seja posta uma bomba nesse block
            if ((mineCol != numLin) || (mineLin != numCol))
            {
                //Verifica se o block que vamos meter mina ja contem mina ou nao
                if (blocos[mineLin][mineCol].gethasMine())
                {
                    Mines--; // Ja existe bomba, entao anulamos a contagem do for
                }
                //se nao tinha mina, vamos entao meter
                else
                {
                    blocos[mineLin][mineCol].setisMined();

                    //IF's para verificar se nao estamos num block nos limites do campo de jogo
                    if (mineCol != 0 && mineCol != (numC-1) && mineLin != 0 && mineLin != (numL-1) )
                        MinesAllaround(mineLin, mineCol);
                    else {
                        // IF para caso estejamos na coluna da esquerda
                        if (mineCol == 0) {
                            if (mineLin == 0) {
                                MinesCol0Lin0(mineLin, mineCol);
                            } else {
                                if (mineLin == numL-1)
                                    MinesCol0LinMax(mineLin, mineCol);

                                else
                                    MinesCol0(mineLin, mineCol);
                            }
                        }

                        //If para caso estejamos na coluna da direita
                        if (mineCol == numC-1) {
                            if (mineLin == 0) {
                                MinesColMaxLin0(mineLin, mineCol);
                            } else {
                                if (mineLin == numL-1)
                                    MinesColMaxLinMax(mineLin, mineCol);

                                else
                                    MinesColmax(mineLin, mineCol);
                            }
                        }

                        //If para caso estejamos na primeira linha
                        if (mineLin == 0 && (mineCol != 0 && mineCol != numC-1)) {
                            MinesLin0(mineLin, mineCol);
                        }

                        //If para caso estejamos na ultima linha
                        if (mineLin == numL-1 && (mineCol != 0 && mineCol != numC-1)) {
                            MinesLinmax(mineLin, mineCol);
                        }
                    }
                }
            }
            // Se e o block em que o utilizador cliclou anulamos a contagem
            else
            {
                Mines--;
            }
        }
    }

    private void MinesCol0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados a esquerda do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  B | 3
        *  4 | 5
        */

        if (!blocos[lin-1][col].gethasMine())
            blocos[lin-1][col].setNumberOfMinesInSurrounding(blocos[lin-1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col+1].gethasMine())
            blocos[lin-1][col+1].setNumberOfMinesInSurrounding(blocos[lin-1][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col+1].gethasMine())
            blocos[lin][col+1].setNumberOfMinesInSurrounding(blocos[lin][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col].gethasMine())
            blocos[lin+1][col].setNumberOfMinesInSurrounding(blocos[lin+1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col+1].gethasMine())
            blocos[lin+1][col+1].setNumberOfMinesInSurrounding(blocos[lin+1][col+1].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesColmax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados a direita do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  3 | B
        *  4 | 5
        */

        if (!blocos[lin-1][col-1].gethasMine())
            blocos[lin-1][col-1].setNumberOfMinesInSurrounding(blocos[lin-1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col].gethasMine())
            blocos[lin-1][col].setNumberOfMinesInSurrounding(blocos[lin-1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col-1].gethasMine())
            blocos[lin][col-1].setNumberOfMinesInSurrounding(blocos[lin][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col-1].gethasMine())
            blocos[lin+1][col-1].setNumberOfMinesInSurrounding(blocos[lin+1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col].gethasMine())
            blocos[lin+1][col].setNumberOfMinesInSurrounding(blocos[lin+1][col].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesLin0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao cimo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | B | 2
        *  3 | 4 | 5
        *
        */

        if (!blocos[lin][col-1].gethasMine())
            blocos[lin][col-1].setNumberOfMinesInSurrounding(blocos[lin][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col+1].gethasMine())
            blocos[lin][col+1].setNumberOfMinesInSurrounding(blocos[lin][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col-1].gethasMine())
            blocos[lin+1][col-1].setNumberOfMinesInSurrounding(blocos[lin+1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col].gethasMine())
            blocos[lin+1][col].setNumberOfMinesInSurrounding(blocos[lin+1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col+1].gethasMine())
            blocos[lin+1][col+1].setNumberOfMinesInSurrounding(blocos[lin+1][col+1].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesLinmax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao fundo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2 | 3
        *  4 | B | 5
        *
        */

        if (!blocos[lin-1][col-1].gethasMine())
            blocos[lin-1][col-1].setNumberOfMinesInSurrounding(blocos[lin-1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col].gethasMine())
            blocos[lin-1][col].setNumberOfMinesInSurrounding(blocos[lin][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col+1].gethasMine())
            blocos[lin-1][col+1].setNumberOfMinesInSurrounding(blocos[lin-1][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col-1].gethasMine())
            blocos[lin][col-1].setNumberOfMinesInSurrounding(blocos[lin][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col+1].gethasMine())
            blocos[lin][col+1].setNumberOfMinesInSurrounding(blocos[lin][col+1].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesCol0Lin0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto superior esquerdo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  B | 1
        *  2 | 3
        *
        */

        if (!blocos[lin][col+1].gethasMine())
            blocos[lin][col+1].setNumberOfMinesInSurrounding(blocos[lin][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col].gethasMine())
            blocos[lin+1][col].setNumberOfMinesInSurrounding(blocos[lin+1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col+1].gethasMine())
            blocos[lin+1][col+1].setNumberOfMinesInSurrounding(blocos[lin+1][col+1].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesCol0LinMax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto inferior esquerdo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  B | 3
        *
        */

        if (!blocos[lin-1][col].gethasMine())
            blocos[lin-1][col].setNumberOfMinesInSurrounding(blocos[lin-1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col+1].gethasMine())
            blocos[lin-1][col+1].setNumberOfMinesInSurrounding(blocos[lin-1][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col+1].gethasMine())
            blocos[lin][col+1].setNumberOfMinesInSurrounding(blocos[lin][col+1].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesColMaxLin0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto superior direito do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | B
        *  2 | 3
        *
        */

        if (!blocos[lin][col-1].gethasMine())
            blocos[lin][col-1].setNumberOfMinesInSurrounding(blocos[lin][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col-1].gethasMine())
            blocos[lin+1][col-1].setNumberOfMinesInSurrounding(blocos[lin+1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col].gethasMine())
            blocos[lin+1][col].setNumberOfMinesInSurrounding(blocos[lin+1][col].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesColMaxLinMax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto inferior direito do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  3 | B
        *
        */

        if (!blocos[lin-1][col-1].gethasMine())
            blocos[lin-1][col-1].setNumberOfMinesInSurrounding(blocos[lin-1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col].gethasMine())
            blocos[lin-1][col].setNumberOfMinesInSurrounding(blocos[lin-1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col-1].gethasMine())
            blocos[lin][col-1].setNumberOfMinesInSurrounding(blocos[lin][col-1].getNumberOfMinesInSorrounding()+1);
    }

    private void MinesAllaround(int lin, int col)
    {

        /*
        * No caso de estarmos encostados a esquerda do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2 | 3
        *  4 | B | 5
        *  6 | 7 | 8
        */

        if (!blocos[lin-1][col-1].gethasMine())
            blocos[lin-1][col-1].setNumberOfMinesInSurrounding(blocos[lin-1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col].gethasMine())
            blocos[lin-1][col].setNumberOfMinesInSurrounding(blocos[lin-1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin-1][col+1].gethasMine())
            blocos[lin-1][col+1].setNumberOfMinesInSurrounding(blocos[lin-1][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col-1].gethasMine())
            blocos[lin][col-1].setNumberOfMinesInSurrounding(blocos[lin][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin][col+1].gethasMine())
            blocos[lin][col+1].setNumberOfMinesInSurrounding(blocos[lin][col+1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col-1].gethasMine())
            blocos[lin+1][col-1].setNumberOfMinesInSurrounding(blocos[lin+1][col-1].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col].gethasMine())
            blocos[lin+1][col].setNumberOfMinesInSurrounding(blocos[lin+1][col].getNumberOfMinesInSorrounding()+1);

        if (!blocos[lin+1][col+1].gethasMine())
            blocos[lin+1][col+1].setNumberOfMinesInSurrounding(blocos[lin+1][col+1].getNumberOfMinesInSorrounding()+1);
    }


    //******************************/
    /*
    *
    *
    */
    //******************************//
    private void rippleUncover(int rowClicked, int columnClicked)
    {

        ArrayList<Integer> tempblockLinha = new ArrayList<>();
        ArrayList<Integer> tempblockColuna = new ArrayList<>();

        // caso o block tenho bomba ou flag nao queremos abrir os block que estao perto sem bombas, pois caso tenha bomba a verificacao e feita em outro IF
        if (blocos[rowClicked][columnClicked].gethasMine() || blocos[rowClicked][columnClicked].getisFlagged())
        {
            return;
        }

        // Abre o block clicado
        blocos[rowClicked][columnClicked].OpenBlock();

        // Se o bloco tiver mines perto dele, nao continuamos a funcao
        if (blocos[rowClicked][columnClicked].getNumberOfMinesInSorrounding() != 0 )
        {
            return;
        }
        else
        {
            //IF's para verificar se nao estamos num block nos limites do campo de jogo
            if (columnClicked != 0 && columnClicked != (numC-1) && rowClicked != 0 && rowClicked != (numL-1) )
                openAllaround(rowClicked, columnClicked);
            else {
                // IF para caso estejamos na coluna da esquerda
                if (columnClicked == 0) {
                    if (rowClicked == 0) {
                        openCol0Lin0(rowClicked, columnClicked);
                    } else {
                        if (rowClicked == numL-1)
                            openCol0LinMax(rowClicked, columnClicked);

                        else
                            openCol0(rowClicked, columnClicked);
                    }
                }

                //If para caso estejamos na coluna da direita
                if (columnClicked == numC-1) {
                    if (rowClicked == 0) {
                        openColMaxLin0(rowClicked, columnClicked);
                    } else {
                        if (rowClicked == numL-1)
                            openColMaxLinMax(rowClicked, columnClicked);

                        else
                            openColmax(rowClicked, columnClicked);
                    }
                }

                //If para caso estejamos na primeira linha
                if (rowClicked == 0 && (columnClicked != 0 && columnClicked != numC-1)) {
                    openLin0(rowClicked, columnClicked);
                }

                //If para caso estejamos na ultima linha
                if (rowClicked == numL-1 && (columnClicked != 0 && columnClicked != numC-1)) {
                    openLinmax(rowClicked, columnClicked);
                }
            }
        }

        while (blockLinha.size() != 0 && blockColuna.size() != 0)
        {

            tempblockColuna.addAll(blockColuna);
            tempblockLinha.addAll(blockLinha);

            blockLinha.removeAll(blockLinha);
            blockLinha.clear();
            blockColuna.removeAll(blockColuna);
            blockColuna.clear();

            for (int i = 0 ; i < tempblockLinha.size() ; i++){

                //IF's para verificar se nao estamos num block nos limites do campo de jogo
                if (tempblockColuna.get(i) != 0 && tempblockColuna.get(i) != (numC-1) && tempblockLinha.get(i) != 0 && tempblockLinha.get(i) != (numL-1) )
                    openAllaround(tempblockLinha.get(i), tempblockColuna.get(i));
                else {
                    // IF para caso estejamos na coluna da esquerda
                    if (tempblockColuna.get(i) == 0) {
                        if (tempblockLinha.get(i) == 0) {
                            openCol0Lin0(tempblockLinha.get(i), tempblockColuna.get(i));
                        } else {
                            if (tempblockLinha.get(i) == numL-1)
                                openCol0LinMax(tempblockLinha.get(i), tempblockColuna.get(i));

                            else
                                openCol0(tempblockLinha.get(i), tempblockColuna.get(i));
                        }
                    }

                    //If para caso estejamos na coluna da direita
                    if (tempblockColuna.get(i) == numC-1) {
                        if (tempblockLinha.get(i) == 0) {
                            openColMaxLin0(tempblockLinha.get(i), tempblockColuna.get(i));
                        } else {
                            if (tempblockLinha.get(i) == numL-1)
                                openColMaxLinMax(tempblockLinha.get(i), tempblockColuna.get(i));

                            else
                                openColmax(tempblockLinha.get(i), tempblockColuna.get(i));
                        }
                    }

                    //If para caso estejamos na primeira linha
                    if (tempblockLinha.get(i) == 0 && (tempblockColuna.get(i) != 0 && tempblockColuna.get(i) != numC-1)) {
                        openLin0(tempblockLinha.get(i), tempblockColuna.get(i));
                    }

                    //If para caso estejamos na ultima linha
                    if (tempblockLinha.get(i) == numL-1 && (tempblockColuna.get(i) != 0 && tempblockColuna.get(i) != numC-1)) {
                        openLinmax(tempblockLinha.get(i), tempblockColuna.get(i));
                    }
                }

            }

            tempblockColuna.removeAll(tempblockColuna);
            tempblockColuna.clear();
            tempblockLinha.removeAll(tempblockLinha);
            tempblockLinha.clear();

        }

    }

    //******************************/
    /*
    *   Funcoes que fazem abres os block's a volta do bloco clicado em que este tenha 0 bombas a sua volta
    */
    //******************************//
    private void openCol0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados a esquerda do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  B | 3
        *  4 | 5
        */

        if (blocos[lin-1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col);
        }
        if (blocos[lin-1][col].getisCovered() && !blocos[lin-1][col].getisFlagged())
            blocos[lin-1][col].OpenBlock();

        if (blocos[lin-1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col+1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin-1][col+1].getisCovered() && !blocos[lin-1][col+1].getisFlagged())
            blocos[lin-1][col+1].OpenBlock();

        if (blocos[lin][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col+1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col + 1);
        }
        if (blocos[lin][col+1].getisCovered() && !blocos[lin][col+1].getisFlagged())
            blocos[lin][col+1].OpenBlock();

        if (blocos[lin+1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col);
        }
        if (blocos[lin+1][col].getisCovered() && !blocos[lin+1][col].getisFlagged())
            blocos[lin+1][col].OpenBlock();

        if (blocos[lin+1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col+1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin+1][col+1].getisCovered() && !blocos[lin+1][col+1].getisFlagged())
            blocos[lin+1][col+1].OpenBlock();
    }

    private void openColmax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados a direita do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  3 | B
        *  4 | 5
        */

        if (blocos[lin-1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col-1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin-1][col-1].getisCovered() && !blocos[lin-1][col-1].getisFlagged())
            blocos[lin-1][col-1].OpenBlock();


        if (blocos[lin-1][col].getNumberOfMinesInSorrounding() == 0  && blocos[lin-1][col].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col);
        }
        if (blocos[lin-1][col].getisCovered() && !blocos[lin-1][col].getisFlagged())
            blocos[lin-1][col].OpenBlock();


        if (blocos[lin][col-1].getNumberOfMinesInSorrounding() == 0  && blocos[lin][col-1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col - 1);
        }
        if (blocos[lin][col-1].getisCovered() && !blocos[lin][col-1].getisFlagged())
            blocos[lin][col-1].OpenBlock();


        if (blocos[lin+1][col-1].getNumberOfMinesInSorrounding() == 0  && blocos[lin+1][col-1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin+1][col-1].getisCovered() && !blocos[lin+1][col-1].getisFlagged())
            blocos[lin+1][col-1].OpenBlock();

        if (blocos[lin+1][col].getNumberOfMinesInSorrounding() == 0  && blocos[lin+1][col].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col);
        }
        if (blocos[lin+1][col].getisCovered() && !blocos[lin+1][col].getisFlagged())
            blocos[lin+1][col].OpenBlock();
    }

    private void openLin0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao cimo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | B | 2
        *  3 | 4 | 5
        *
        */

        if (blocos[lin][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col-1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col - 1);
        }
        if (blocos[lin][col-1].getisCovered() && !blocos[lin][col-1].getisFlagged())
            blocos[lin][col-1].OpenBlock();

        if (blocos[lin][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col+1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col + 1);
        }
        if (blocos[lin][col+1].getisCovered() && !blocos[lin][col+1].getisFlagged())
            blocos[lin][col+1].OpenBlock();

        if (blocos[lin+1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col-1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin+1][col-1].getisCovered() && !blocos[lin+1][col-1].getisFlagged())
            blocos[lin+1][col-1].OpenBlock();

        if (blocos[lin+1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col);
        }
        if (blocos[lin+1][col].getisCovered() && !blocos[lin+1][col].getisFlagged())
            blocos[lin+1][col].OpenBlock();

        if (blocos[lin+1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col+1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin+1][col+1].getisCovered() && !blocos[lin+1][col+1].getisFlagged())
            blocos[lin+1][col+1].OpenBlock();
    }

    private void openLinmax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao fundo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2 | 3
        *  4 | B | 5
        *
        */

        if (blocos[lin-1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col-1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin-1][col-1].getisCovered() && !blocos[lin-1][col-1].getisFlagged())
            blocos[lin-1][col-1].OpenBlock();

        if (blocos[lin-1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col);
        }
        if (blocos[lin-1][col].getisCovered() && !blocos[lin-1][col].getisFlagged())
            blocos[lin-1][col].OpenBlock();

        if (blocos[lin-1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col+1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin-1][col+1].getisCovered() && !blocos[lin-1][col+1].getisFlagged())
            blocos[lin-1][col+1].OpenBlock();

        if (blocos[lin][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col-1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col - 1);
        }
        if (blocos[lin][col-1].getisCovered() && !blocos[lin][col-1].getisFlagged())
            blocos[lin][col-1].OpenBlock();

        if (blocos[lin][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col+1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col + 1);
        }
        if (blocos[lin][col+1].getisCovered() && !blocos[lin][col+1].getisFlagged())
            blocos[lin][col+1].OpenBlock();

    }

    private void openCol0Lin0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto superior esquerdo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  B | 1
        *  2 | 3
        *
        */

        if (blocos[lin][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col+1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col + 1);
        }
        if (blocos[lin][col+1].getisCovered() && !blocos[lin][col+1].getisFlagged())
            blocos[lin][col+1].OpenBlock();

        if (blocos[lin+1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col);
        }
        if (blocos[lin+1][col].getisCovered() && !blocos[lin+1][col].getisFlagged())
            blocos[lin+1][col].OpenBlock();

        if (blocos[lin+1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col+1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin+1][col+1].getisCovered() && !blocos[lin+1][col+1].getisFlagged())
            blocos[lin+1][col+1].OpenBlock();

    }

    private void openCol0LinMax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto inferior esquerdo do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  B | 3
        *
        */

        if (blocos[lin-1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col);
        }
        if (blocos[lin-1][col].getisCovered() && !blocos[lin-1][col].getisFlagged())
            blocos[lin-1][col].OpenBlock();

        if (blocos[lin-1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col+1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin-1][col+1].getisCovered() && !blocos[lin-1][col+1].getisFlagged())
            blocos[lin-1][col+1].OpenBlock();

        if (blocos[lin][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col+1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col + 1);
        }
        if (blocos[lin][col+1].getisCovered() && !blocos[lin][col+1].getisFlagged())
            blocos[lin][col+1].OpenBlock();

    }

    private void openColMaxLin0(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto superior direito do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | B
        *  2 | 3
        *
        */

        if (blocos[lin][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col-1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col - 1);
        }
        if (blocos[lin][col-1].getisCovered() && !blocos[lin][col-1].getisFlagged())
            blocos[lin][col-1].OpenBlock();

        if (blocos[lin+1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col-1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin+1][col-1].getisCovered() && !blocos[lin+1][col-1].getisFlagged())
            blocos[lin+1][col-1].OpenBlock();

        if (blocos[lin+1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col);
        }
        if (blocos[lin+1][col].getisCovered() && !blocos[lin+1][col].getisFlagged())
            blocos[lin+1][col].OpenBlock();

    }

    private void openColMaxLinMax(int lin, int col)
    {

        /*
        * No caso de estarmos encostados ao canto inferior direito do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2
        *  3 | B
        *
        */

        if (blocos[lin-1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col-1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin-1][col-1].getisCovered() && !blocos[lin-1][col-1].getisFlagged())
            blocos[lin-1][col-1].OpenBlock();

        if (blocos[lin-1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col);
        }
        if (blocos[lin-1][col].getisCovered() && !blocos[lin-1][col].getisFlagged())
            blocos[lin-1][col].OpenBlock();

        if (blocos[lin][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col-1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col - 1);
        }
        if (blocos[lin][col-1].getisCovered() && !blocos[lin][col-1].getisFlagged())
            blocos[lin][col-1].OpenBlock();

    }

    private void openAllaround(int lin, int col)
    {

        /*
        * No caso de estarmos encostados a esquerda do campo de jogo verificamos apenas 5 casas
        * e acrecentamos mais 1 a contagem de bombas perto do block, caso esta nao seja uma bomba
        *
        *  1 | 2 | 3
        *  4 | B | 5
        *  6 | 7 | 8
        */

        if (blocos[lin-1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col-1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin-1][col-1].getisCovered() && !blocos[lin-1][col-1].getisFlagged())
            blocos[lin-1][col-1].OpenBlock();

        if (blocos[lin-1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col);
        }
        if (blocos[lin-1][col].getisCovered() && !blocos[lin-1][col].getisFlagged())
            blocos[lin-1][col].OpenBlock();

        if (blocos[lin-1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin-1][col+1].getisCovered()) {
            blockLinha.add(lin - 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin-1][col+1].getisCovered() && !blocos[lin-1][col+1].getisFlagged())
            blocos[lin-1][col+1].OpenBlock();

        if (blocos[lin][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col-1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col - 1);
        }
        if (blocos[lin][col-1].getisCovered() && !blocos[lin][col-1].getisFlagged())
            blocos[lin][col-1].OpenBlock();

        if (blocos[lin][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin][col+1].getisCovered()) {
            blockLinha.add(lin);
            blockColuna.add(col + 1);
        }
        if (blocos[lin][col+1].getisCovered() && !blocos[lin][col+1].getisFlagged())
            blocos[lin][col+1].OpenBlock();

        if (blocos[lin+1][col-1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col-1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col - 1);
        }
        if (blocos[lin+1][col-1].getisCovered() && !blocos[lin+1][col-1].getisFlagged())
            blocos[lin+1][col-1].OpenBlock();

        if (blocos[lin+1][col].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col);
        }
        if (blocos[lin+1][col].getisCovered() && !blocos[lin+1][col].getisFlagged())
            blocos[lin+1][col].OpenBlock();

        if (blocos[lin+1][col+1].getNumberOfMinesInSorrounding() == 0 && blocos[lin+1][col+1].getisCovered()) {
            blockLinha.add(lin + 1);
            blockColuna.add(col + 1);
        }
        if (blocos[lin+1][col+1].getisCovered() && !blocos[lin+1][col+1].getisFlagged())
            blocos[lin+1][col+1].OpenBlock();
    }



    //******************************/
    /*
    *   Começa o contador de tempo
    */
    //******************************//
    public void startTimer()
    {
        if (secondsPassed == 0)
        {
            timer.removeCallbacks(updateTimeElasped);
            // Define que o Timer faça a contagem de 1seg
            timer.postDelayed(updateTimeElasped, 1000);
        }
    }

    //******************************//
    /*
    *   Para o contador de tempo
    */
    //******************************//
    public void stopTimer()
    {
        timer.removeCallbacks(updateTimeElasped);
    }


    //******************************//
    /*
    *   Funcao para actualizar o contador no ecran
    */
    //******************************//
    private Runnable updateTimeElasped = new Runnable()
    {
        public void run()
        {
            long currentMilliseconds = System.currentTimeMillis();
            ++secondsPassed;

            if (secondsPassed < 10)
            {
                txtTimer.setText(getString(R.string.doiszeros) + Integer.toString(secondsPassed));
            }
            else if (secondsPassed < 100)
            {
                txtTimer.setText(getString(R.string.zero) + Integer.toString(secondsPassed));
            }
            else
            {
                txtTimer.setText(Integer.toString(secondsPassed));
            }

            // Adiciona uma notificacao para que esta funcao seja chamada a cada segundo que passa
            timer.postAtTime(this, currentMilliseconds);
            timer.postDelayed(updateTimeElasped, 1000);
        }
    };


    //******************************//
    /*
    *   Funcao para actualizar o contador no bombas
    */
    //******************************//
    private void updateMineCountDisplay()
    {
        if (minesToFind < 0)
        {
            txtScore.setText(Integer.toString(minesToFind));
        }
        else if (minesToFind < 10)
        {
            txtScore.setText("00" + Integer.toString(minesToFind));
        }
        else if (minesToFind < 100)
        {
            txtScore.setText("0" + Integer.toString(minesToFind));
        }
        else
        {
            txtScore.setText(Integer.toString(minesToFind));
        }
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
