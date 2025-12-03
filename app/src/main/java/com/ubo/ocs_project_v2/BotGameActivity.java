package com.ubo.ocs_project_v2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class BotGameActivity extends AppCompatActivity {

    private Button[] buttons = new Button[16];
    private TextView joueurTextView;
    private TextView timerTextView;
    private TextView resultTextView;
    private Button btnRejouer;
    private Button btnMenu;

    private boolean joueurTurn = true;
    private int compteurTours = 0;
    private boolean jeuTermine = false;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot_game);

        initViews();
        initListeners();

        resultTextView.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null) {
            restoreGameState(savedInstanceState);
        } else {
            startTimer();
        }
    }

    private void initViews() {
        joueurTextView = findViewById(R.id.Joueur);
        timerTextView = findViewById(R.id.timerTextView);
        resultTextView = findViewById(R.id.textView2);
        btnRejouer = findViewById(R.id.btnRejouer);
        btnMenu = findViewById(R.id.btnMenu);

        buttons[0] = findViewById(R.id.button1);
        buttons[1] = findViewById(R.id.button2);
        buttons[2] = findViewById(R.id.button3);
        buttons[3] = findViewById(R.id.button4);
        buttons[4] = findViewById(R.id.button5);
        buttons[5] = findViewById(R.id.button6);
        buttons[6] = findViewById(R.id.button7);
        buttons[7] = findViewById(R.id.button8);
        buttons[8] = findViewById(R.id.button9);
        buttons[9] = findViewById(R.id.button10);
        buttons[10] = findViewById(R.id.button11);
        buttons[11] = findViewById(R.id.button12);
        buttons[12] = findViewById(R.id.button13);
        buttons[13] = findViewById(R.id.button14);
        buttons[14] = findViewById(R.id.button15);
        buttons[15] = findViewById(R.id.button16);
    }

    private void initListeners() {
        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCellClick(index);
                }
            });
        }

        btnRejouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onCellClick(int index) {
        if (!joueurTurn || jeuTermine) {
            return;
        }

        String texteActuel = buttons[index].getText().toString();
        if (!texteActuel.isEmpty()) {
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        buttons[index].setText("X");
        buttons[index].setTextColor(Color.RED);

        compteurTours++;

        if (checkVictoire()) {
            jeuTermine = true;
            resultTextView.setText("Vous avez gagné !");
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (compteurTours == 16) {
            jeuTermine = true;
            resultTextView.setText("Match nul !");
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        joueurTurn = false;
        joueurTextView.setText("Tour du BOT");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                botPlay();
            }
        }, 1000);
    }

    private void botPlay() {
        if (jeuTermine) {
            return;
        }

        ArrayList<Integer> casesVides = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (buttons[i].getText().toString().isEmpty()) {
                casesVides.add(i);
            }
        }

        if (casesVides.isEmpty()) {
            return;
        }

        Random random = new Random();
        int index = casesVides.get(random.nextInt(casesVides.size()));

        buttons[index].setText("O");
        buttons[index].setTextColor(Color.GREEN);

        compteurTours++;

        if (checkVictoire()) {
            jeuTermine = true;
            resultTextView.setText("BOT a gagné !");
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (compteurTours == 16) {
            jeuTermine = true;
            resultTextView.setText("Match nul !");
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        joueurTurn = true;
        joueurTextView.setText("Votre tour");

        startTimer();
    }

    private void startTimer() {
        timeLeftInMillis = 5000;

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int secondsLeft = (int) (millisUntilFinished / 1000) + 1;
                timerTextView.setText(String.valueOf(secondsLeft));
            }

            @Override
            public void onFinish() {
                if (!jeuTermine && joueurTurn) {
                    jeuTermine = true;
                    resultTextView.setText("Temps écoulé ! BOT gagne !");
                    resultTextView.setVisibility(View.VISIBLE);
                    timerTextView.setText("0");
                }
            }
        }.start();
    }

    private boolean checkVictoire() {
        String[][] grille = new String[4][4];

        for (int i = 0; i < 16; i++) {
            grille[i / 4][i % 4] = buttons[i].getText().toString();
        }

        for (int i = 0; i < 4; i++) {
            if (!grille[i][0].equals("") &&
                    grille[i][0].equals(grille[i][1]) &&
                    grille[i][1].equals(grille[i][2]) &&
                    grille[i][2].equals(grille[i][3])) {
                return true;
            }
        }

        for (int i = 0; i < 4; i++) {
            if (!grille[0][i].equals("") &&
                    grille[0][i].equals(grille[1][i]) &&
                    grille[1][i].equals(grille[2][i]) &&
                    grille[2][i].equals(grille[3][i])) {
                return true;
            }
        }

        if (!grille[0][0].equals("") &&
                grille[0][0].equals(grille[1][1]) &&
                grille[1][1].equals(grille[2][2]) &&
                grille[2][2].equals(grille[3][3])) {
            return true;
        }

        if (!grille[0][3].equals("") &&
                grille[0][3].equals(grille[1][2]) &&
                grille[1][2].equals(grille[2][1]) &&
                grille[2][1].equals(grille[3][0])) {
            return true;
        }

        return false;
    }

    private void resetGame() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        for (Button button : buttons) {
            button.setText("");
            button.setTextColor(Color.BLACK);
        }

        joueurTurn = true;
        compteurTours = 0;
        jeuTermine = false;

        joueurTextView.setText("Votre tour");
        resultTextView.setVisibility(View.INVISIBLE);

        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        outState.putBoolean("joueurTurn", joueurTurn);
        outState.putInt("compteurTours", compteurTours);
        outState.putBoolean("jeuTermine", jeuTermine);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);

        for (int i = 0; i < 16; i++) {
            outState.putString("button" + i, buttons[i].getText().toString());
        }

        outState.putString("resultText", resultTextView.getText().toString());
        outState.putInt("resultVisibility", resultTextView.getVisibility());
    }

    private void restoreGameState(Bundle savedInstanceState) {
        joueurTurn = savedInstanceState.getBoolean("joueurTurn");
        compteurTours = savedInstanceState.getInt("compteurTours");
        jeuTermine = savedInstanceState.getBoolean("jeuTermine");
        timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");

        for (int i = 0; i < 16; i++) {
            String text = savedInstanceState.getString("button" + i);
            buttons[i].setText(text);
            if (text.equals("X")) {
                buttons[i].setTextColor(Color.RED);
            } else if (text.equals("O")) {
                buttons[i].setTextColor(Color.GREEN);
            }
        }

        if (joueurTurn) {
            joueurTextView.setText("Votre tour");
        } else {
            joueurTextView.setText("Tour du BOT");
        }

        resultTextView.setText(savedInstanceState.getString("resultText"));
        resultTextView.setVisibility(savedInstanceState.getInt("resultVisibility"));

        if (!jeuTermine && joueurTurn) {
            startTimer();
        }
    }
}