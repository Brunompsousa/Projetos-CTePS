package com.example.brums.minesweeper_v2_1;

import android.content.Context;
import android.widget.Button;

/**
 * Created by Utilizador on 20/01/2018.
 */

public class Block extends Button{

    //******************************//
    //********* Variaveis **********//
    //******************************//

    private boolean isCovered; // Por clicar?
    private boolean isMined; // Contem bomba?
    private boolean isFlagged; // Está com flag?
    private boolean isQuestionMarked; // '?'?
    private boolean isClickable; // Clicavel?
    private int numberOfMinesInSurrounding; // Numero de minas adjacentes


    //******************************//
    //******** Contrutores *********//
    //******************************//

    public Block(Context context)
    {
        super(context);
    }

    //******************************//
    //*********** Get's ************//
    //******************************//

    public boolean gethasMine()
    {
        return isMined;
    }

    public boolean getisCovered()
    {
        return isCovered;
    }

    public int getNumberOfMinesInSorrounding()
    {
        return numberOfMinesInSurrounding;
    }

    public boolean getisFlagged()
    {
        return isFlagged;
    }

    public boolean getisQuestionMarked()
    {
        return isQuestionMarked;
    }

    public boolean getisClickable()
    {
        return isClickable;
    }


    //******************************//
    //*********** Set's ************//
    //******************************//

    public void setFlagged(boolean flagged)
    {
        isFlagged = flagged;
    }

    //Marca o block como tento bomba
    public void setisMined()
    {
        isMined = true;
    }

    public void setNumberOfMinesInSurrounding(int number)
    {
        numberOfMinesInSurrounding = number;
    }

    public void setQuestionMarked(boolean questionMarked)
    {
        isQuestionMarked = questionMarked;
    }

    public void setClickable(boolean clickable)
    {
        isClickable = clickable;
    }


    //******************************//
    //********** Funções ***********//
    //******************************//

    // Funcao para meter as propriedades base no objecto
    public void setDefaults()
    {
        isCovered = true;
        isMined = false;
        isFlagged = false;
        isQuestionMarked = false;
        isClickable = true;
        numberOfMinesInSurrounding = 0;

        this.setBackgroundResource(R.drawable.btn);
    }

    // Desenha o block com o numero de bombas adjacents
    public void setNumberOfSurroundingMines(int number)
    {
        switch (numberOfMinesInSurrounding){

            case 1:
                this.setBackgroundResource(R.drawable.um);
                break;
            case 2:
                this.setBackgroundResource(R.drawable.dois);
                break;
            case 3:
                this.setBackgroundResource(R.drawable.tres);
                break;
            case 4:
                this.setBackgroundResource(R.drawable.quatro);
                break;
            case 5:
                this.setBackgroundResource(R.drawable.cinco);
                break;
            case 6:
                this.setBackgroundResource(R.drawable.seis);
                break;
            case 7:
                this.setBackgroundResource(R.drawable.sete);
                break;
            case 8:
                this.setBackgroundResource(R.drawable.oito);
                break;
            default:
                this.setBackgroundResource(R.drawable.afterclick);
                break;


        }
    }

    //Se receber true desenha o block como bomba
    public void setMineIcon(boolean enabled)
    {
        if (enabled)
        {
            this.setBackgroundResource(R.drawable.bomb);
        }

    }

    // Metes block como flag se o setflag for true
    public void setFlagIcon(boolean enabled)
    {
        if (enabled)
        {
            this.setBackgroundResource(R.drawable.flag);
        }

    }

    // Mete block como '?' se o questmark for true
    public void setQuestionMarkIcon(boolean enabled)
    {
        if (enabled)
        {
            this.setBackgroundResource(R.drawable.questionmark);
        }
    }

    // Mete o block como afterclick se for true, senão o aspecto será para bnt
    public void setBlockAsDisabled(boolean enabled)
    {
        if (enabled)
        {
            setClickable(false);
            this.setBackgroundResource(R.drawable.afterclick);
        }
        else
        {
            this.setBackgroundResource(R.drawable.btn);
        }
    }

    // mostrar o conteudo do block
    public void OpenBlock()
    {
        // cannot uncover a mine which is not covered
        if (!isCovered)
            return;

        setBlockAsDisabled(true);
        isCovered = false;

        // se for mine
        if (gethasMine())
        {
            setMineIcon(true);
        }
        // senao nao mete o numero de bombas que o block tem a sua volta
        else
        {
            setNumberOfSurroundingMines(numberOfMinesInSurrounding);
        }
    }

    // Quando o block com a mina e aberto
    public void triggerMine()
    {
        setMineIcon(true);
        this.setBackgroundResource(R.drawable.explosion);
    }

}
