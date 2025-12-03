package com.ubo.Morpion;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

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

    private static final int MAX_DEPTH = 2;

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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        String[] currentState = new String[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            currentState[i] = buttons[i].getText().toString();
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        String savedResultText = resultTextView.getText().toString();
        int savedResultVisibility = resultTextView.getVisibility();
        int savedResultColor = resultTextView.getCurrentTextColor();

        setContentView(R.layout.activity_bot_game);

        initViews();
        initListeners();

        for (int i = 0; i < buttons.length && i < currentState.length; i++) {
            buttons[i].setText(currentState[i]);
            if (currentState[i].equals("X")) {
                buttons[i].setTextColor(Color.RED);
            } else if (currentState[i].equals("O")) {
                buttons[i].setTextColor(Color.GREEN);
            }
        }

        joueurTextView.setText(joueurTurn ? "Votre tour" : "Tour du BOT");

        resultTextView.setText(savedResultText);
        resultTextView.setTextColor(savedResultColor);
        resultTextView.setVisibility(savedResultVisibility);

        int secondsLeft = (int) (timeLeftInMillis / 1000) + 1;
        if (jeuTermine || timeLeftInMillis <= 0) {
            timerTextView.setText("0");
        } else {
            timerTextView.setText(String.valueOf(secondsLeft));
            if (joueurTurn && !jeuTermine) {
                continueTimer();
            }
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
            resultTextView.setTextColor(Color.RED);
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (compteurTours == 16) {
            jeuTermine = true;
            resultTextView.setText("Match nul !");
            resultTextView.setTextColor(Color.BLACK);
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

        // Get the best move using Minimax algorithm
        int bestMove = getBestMove();

        if (bestMove != -1) {
            buttons[bestMove].setText("O");
            buttons[bestMove].setTextColor(Color.GREEN);

            compteurTours++;

            if (checkVictoire()) {
                jeuTermine = true;
                resultTextView.setText("BOT a gagné !");
                resultTextView.setTextColor(Color.GREEN);
                resultTextView.setVisibility(View.VISIBLE);
                return;
            }

            if (compteurTours == 16) {
                jeuTermine = true;
                resultTextView.setText("Match nul !");
                resultTextView.setTextColor(Color.BLACK);
                resultTextView.setVisibility(View.VISIBLE);
                return;
            }
        }

        joueurTurn = true;
        joueurTextView.setText("Votre tour");

        startTimer();
    }

    // Minimax Algorithm with Alpha-Beta Pruning
    private int getBestMove() {
        String[][] board = getBoardState();
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        ArrayList<Integer> emptyCells = getEmptyCells(board);

        // If it's the first move, choose a strategic position
        if (compteurTours == 1) {
            // Prefer center or corners
            int[] strategicMoves = {5, 6, 9, 10, 0, 3, 12, 15}; // Center cells first, then corners
            for (int move : strategicMoves) {
                if (board[move / 4][move % 4].isEmpty()) {
                    return move;
                }
            }
        }

        for (int cell : emptyCells) {
            int row = cell / 4;
            int col = cell % 4;

            board[row][col] = "O";
            int score = minimax(board, 0, false, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board[row][col] = "";

            if (score > bestScore) {
                bestScore = score;
                bestMove = cell;
            }
        }

        return bestMove;
    }

    private int minimax(String[][] board, int depth, boolean isMaximizing, int alpha, int beta) {
        // Check terminal states
        String winner = checkWinner(board);
        if (winner.equals("O")) {
            return 100 - depth; // Prefer faster wins
        } else if (winner.equals("X")) {
            return depth - 100; // Prefer slower losses
        } else if (isBoardFull(board) || depth >= MAX_DEPTH) {
            return evaluateBoard(board); // Use heuristic evaluation
        }

        ArrayList<Integer> emptyCells = getEmptyCells(board);

        if (isMaximizing) {
            int maxScore = Integer.MIN_VALUE;
            for (int cell : emptyCells) {
                int row = cell / 4;
                int col = cell % 4;

                board[row][col] = "O";
                int score = minimax(board, depth + 1, false, alpha, beta);
                board[row][col] = "";

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);

                if (beta <= alpha) {
                    break; // Beta cutoff
                }
            }
            return maxScore;
        } else {
            int minScore = Integer.MAX_VALUE;
            for (int cell : emptyCells) {
                int row = cell / 4;
                int col = cell % 4;

                board[row][col] = "X";
                int score = minimax(board, depth + 1, true, alpha, beta);
                board[row][col] = "";

                minScore = Math.min(minScore, score);
                beta = Math.min(beta, score);

                if (beta <= alpha) {
                    break; // Alpha cutoff
                }
            }
            return minScore;
        }
    }

    // Heuristic evaluation function
    private int evaluateBoard(String[][] board) {
        int score = 0;

        // Evaluate all possible winning lines
        score += evaluateLine(board, 0, 0, 0, 1, 4); // Rows
        score += evaluateLine(board, 1, 0, 0, 1, 4);
        score += evaluateLine(board, 2, 0, 0, 1, 4);
        score += evaluateLine(board, 3, 0, 0, 1, 4);

        score += evaluateLine(board, 0, 0, 1, 0, 4); // Columns
        score += evaluateLine(board, 0, 1, 1, 0, 4);
        score += evaluateLine(board, 0, 2, 1, 0, 4);
        score += evaluateLine(board, 0, 3, 1, 0, 4);

        score += evaluateLine(board, 0, 0, 1, 1, 4); // Diagonals
        score += evaluateLine(board, 0, 3, 1, -1, 4);

        return score;
    }

    private int evaluateLine(String[][] board, int startRow, int startCol, int dRow, int dCol, int length) {
        int botCount = 0;
        int playerCount = 0;

        for (int i = 0; i < length; i++) {
            int row = startRow + i * dRow;
            int col = startCol + i * dCol;

            if (board[row][col].equals("O")) {
                botCount++;
            } else if (board[row][col].equals("X")) {
                playerCount++;
            }
        }

        // If both players have pieces in this line, it's blocked
        if (botCount > 0 && playerCount > 0) {
            return 0;
        }

        // Score based on number of pieces
        if (botCount == 3) return 50;
        if (botCount == 2) return 10;
        if (botCount == 1) return 1;

        if (playerCount == 3) return -50;
        if (playerCount == 2) return -10;
        if (playerCount == 1) return -1;

        return 0;
    }

    private String checkWinner(String[][] board) {
        // Check rows
        for (int i = 0; i < 4; i++) {
            if (!board[i][0].isEmpty() &&
                    board[i][0].equals(board[i][1]) &&
                    board[i][1].equals(board[i][2]) &&
                    board[i][2].equals(board[i][3])) {
                return board[i][0];
            }
        }

        // Check columns
        for (int i = 0; i < 4; i++) {
            if (!board[0][i].isEmpty() &&
                    board[0][i].equals(board[1][i]) &&
                    board[1][i].equals(board[2][i]) &&
                    board[2][i].equals(board[3][i])) {
                return board[0][i];
            }
        }

        // Check diagonals
        if (!board[0][0].isEmpty() &&
                board[0][0].equals(board[1][1]) &&
                board[1][1].equals(board[2][2]) &&
                board[2][2].equals(board[3][3])) {
            return board[0][0];
        }

        if (!board[0][3].isEmpty() &&
                board[0][3].equals(board[1][2]) &&
                board[1][2].equals(board[2][1]) &&
                board[2][1].equals(board[3][0])) {
            return board[0][3];
        }

        return "";
    }

    private boolean isBoardFull(String[][] board) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private String[][] getBoardState() {
        String[][] board = new String[4][4];
        for (int i = 0; i < 16; i++) {
            board[i / 4][i % 4] = buttons[i].getText().toString();
        }
        return board;
    }

    private ArrayList<Integer> getEmptyCells(String[][] board) {
        ArrayList<Integer> emptyCells = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (board[i][j].isEmpty()) {
                    emptyCells.add(i * 4 + j);
                }
            }
        }
        return emptyCells;
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

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
                    resultTextView.setText("Temps écoulé! le BOT à gagné!");
                    resultTextView.setTextColor(Color.GREEN);
                    resultTextView.setVisibility(View.VISIBLE);
                    timerTextView.setText("0");
                }
            }
        }.start();
    }

    private void continueTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (timeLeftInMillis <= 0) {
            timeLeftInMillis = 5000;
        }

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
                    resultTextView.setTextColor(Color.GREEN);
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

    private void saveCurrentState(Bundle outState) {
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
        outState.putInt("resultColor", resultTextView.getCurrentTextColor());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveCurrentState(outState);
    }

    private void restoreGameState(Bundle savedInstanceState) {
        joueurTurn = savedInstanceState.getBoolean("joueurTurn", true);
        compteurTours = savedInstanceState.getInt("compteurTours", 0);
        jeuTermine = savedInstanceState.getBoolean("jeuTermine", false);
        timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis", 5000);

        for (int i = 0; i < 16; i++) {
            String text = savedInstanceState.getString("button" + i, "");
            buttons[i].setText(text);
            if (text.equals("X")) {
                buttons[i].setTextColor(Color.RED);
            } else if (text.equals("O")) {
                buttons[i].setTextColor(Color.GREEN);
            } else {
                buttons[i].setTextColor(Color.BLACK);
            }
        }

        String joueurText = savedInstanceState.getString("joueurText", "Votre tour");
        if (joueurTurn) {
            joueurTextView.setText("Votre tour");
        } else {
            joueurTextView.setText("Tour du BOT");
        }

        String resultText = savedInstanceState.getString("resultText", "Résultat");
        resultTextView.setText(resultText);
        int resultColor = savedInstanceState.getInt("resultColor", Color.BLACK);
        resultTextView.setTextColor(resultColor);
        int resultVisibility = savedInstanceState.getInt("resultVisibility", View.INVISIBLE);
        resultTextView.setVisibility(resultVisibility);

        int secondsLeft = (int) (timeLeftInMillis / 1000) + 1;
        if (jeuTermine || timeLeftInMillis <= 0) {
            timerTextView.setText("0");
        } else {
            timerTextView.setText(String.valueOf(secondsLeft));
        }

        if (!jeuTermine && joueurTurn) {
            continueTimer();
        }
    }
}