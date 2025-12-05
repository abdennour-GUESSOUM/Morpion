package com.ubo.Morpion;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private TextView joueurTextView;
    private TextView resultTextView;
    private Button btnRejouer;
    private Button btnMenu;

    private Button[] buttons;
    private int gridSize;
    private boolean joueur1Turn = true;
    private int compteurTours = 0;
    private boolean jeuTermine = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridSize = getIntent().getIntExtra("GRID_SIZE", 3);

        initViews();

        if (savedInstanceState != null) {
            restoreGameState(savedInstanceState);
        } else {
            createGrid();
        }

        initListeners();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        String[] currentState = new String[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            currentState[i] = buttons[i].getText().toString();
        }

        String savedResultText = resultTextView.getText().toString();
        int savedResultVisibility = resultTextView.getVisibility();
        int savedResultColor = resultTextView.getCurrentTextColor();

        setContentView(R.layout.activity_main);
        initViews();
        createGrid();

        for (int i = 0; i < buttons.length && i < currentState.length; i++) {
            buttons[i].setText(currentState[i]);
            if (currentState[i].equals("X")) {
                buttons[i].setTextColor(ContextCompat.getColor(this, R.color.player_x));
            } else if (currentState[i].equals("O")) {
                buttons[i].setTextColor(ContextCompat.getColor(this, R.color.player_o));
            }
        }

        joueurTextView.setText(joueur1Turn ? "Joueur 1" : "Joueur 2");
        resultTextView.setText(savedResultText);
        resultTextView.setTextColor(savedResultColor);
        resultTextView.setVisibility(savedResultVisibility);
        initListeners();
    }

    private void initViews() {
        gridLayout = findViewById(R.id.gridLayout);
        joueurTextView = findViewById(R.id.Joueur);
        resultTextView = findViewById(R.id.textView2);
        btnRejouer = findViewById(R.id.btnRejouer);
        btnMenu = findViewById(R.id.btnMenu);

        resultTextView.setVisibility(View.INVISIBLE);
    }

    private void createGrid() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);

        int totalCells = gridSize * gridSize;
        buttons = new Button[totalCells];

        int buttonSize = calculateButtonSize();

        int marginInDp = 6;
        int marginInPixels = (int) (marginInDp * getResources().getDisplayMetrics().density);

        for (int i = 0; i < totalCells; i++) {
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setText("");
            button.setTextColor(Color.BLACK);

            int textSize = Math.max(18, Math.min(48, buttonSize / 3));
            button.setTextSize(textSize);
            button.setGravity(android.view.Gravity.CENTER);
            button.setMaxLines(1);

            button.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
            button.setLayoutParams(params);

            final int index = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCellClick(index);
                }
            });

            buttons[i] = button;
            gridLayout.addView(button);
        }
    }

    private int calculateButtonSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int totalMarginPixels = 20 * gridSize;
        int availableSize;

        if (screenHeight > screenWidth) {
            availableSize = screenWidth - 100;
        } else {
            availableSize = (int) (screenHeight * 0.8);
        }

        int buttonSize = (availableSize - totalMarginPixels) / gridSize;
        return buttonSize;
    }

    private void initListeners() {
        btnRejouer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                resetGame();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 200);
            }
        });
    }

    private void animateButton(View button) {
        int originalColor = ContextCompat.getColor(this, R.color.button_default);
        int pressedColor = ContextCompat.getColor(this, R.color.button_pressed);

        button.setBackgroundColor(pressedColor);

        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start();
                        button.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setBackgroundColor(originalColor);
                            }
                        }, 100);
                    }
                })
                .start();
    }

    private void onCellClick(int index) {
        if (jeuTermine || !buttons[index].getText().toString().isEmpty()) {
            return;
        }

        if (joueur1Turn) {
            buttons[index].setText("X");
            buttons[index].setTextColor(ContextCompat.getColor(this, R.color.player_x));
        } else {
            buttons[index].setText("O");
            buttons[index].setTextColor(ContextCompat.getColor(this, R.color.player_o));
        }

        buttons[index].setScaleX(0.5f);
        buttons[index].setScaleY(0.5f);
        buttons[index].animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        compteurTours++;

        if (checkVictoire()) {
            jeuTermine = true;
            String gagnant = joueur1Turn ? "Joueur 1" : "Joueur 2";
            resultTextView.setText(gagnant + " a gagné !");
            resultTextView.setTextColor(joueur1Turn ?
                    ContextCompat.getColor(this, R.color.player_x) :
                    ContextCompat.getColor(this, R.color.player_o));
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (compteurTours == gridSize * gridSize) {
            jeuTermine = true;
            resultTextView.setText("Match nul !");
            resultTextView.setTextColor(Color.BLACK);
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        joueur1Turn = !joueur1Turn;
        joueurTextView.setText(joueur1Turn ? "Joueur 1" : "Joueur 2");
    }

    private boolean checkVictoire() {
        String[][] grille = new String[gridSize][gridSize];

        for (int i = 0; i < gridSize * gridSize; i++) {
            grille[i / gridSize][i % gridSize] = buttons[i].getText().toString();
        }

        for (int i = 0; i < gridSize; i++) {
            if (checkLine(grille, i, 0, 0, 1)) return true;
        }

        for (int i = 0; i < gridSize; i++) {
            if (checkLine(grille, 0, i, 1, 0)) return true;
        }

        if (checkLine(grille, 0, 0, 1, 1)) return true;
        if (checkLine(grille, 0, gridSize - 1, 1, -1)) return true;

        return false;
    }

    private boolean checkLine(String[][] grille, int startRow, int startCol, int rowIncrement, int colIncrement) {
        String first = grille[startRow][startCol];
        if (first.isEmpty()) return false;

        int row = startRow;
        int col = startCol;

        for (int i = 0; i < gridSize; i++) {
            if (!grille[row][col].equals(first)) return false;
            row += rowIncrement;
            col += colIncrement;
        }

        return true;
    }

    private void resetGame() {
        for (Button button : buttons) {
            button.setText("");
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
        }

        joueur1Turn = true;
        compteurTours = 0;
        jeuTermine = false;

        joueurTextView.setText("Joueur 1");
        resultTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("joueur1Turn", joueur1Turn);
        outState.putInt("compteurTours", compteurTours);
        outState.putBoolean("jeuTermine", jeuTermine);
        outState.putInt("gridSize", gridSize);

        for (int i = 0; i < buttons.length; i++) {
            outState.putString("button" + i, buttons[i].getText().toString());
        }

        outState.putString("resultText", resultTextView.getText().toString());
        outState.putInt("resultVisibility", resultTextView.getVisibility());
        outState.putInt("resultColor", resultTextView.getCurrentTextColor());
    }

    private void restoreGameState(Bundle savedInstanceState) {
        joueur1Turn = savedInstanceState.getBoolean("joueur1Turn");
        compteurTours = savedInstanceState.getInt("compteurTours");
        jeuTermine = savedInstanceState.getBoolean("jeuTermine");

        createGrid();

        for (int i = 0; i < buttons.length; i++) {
            String text = savedInstanceState.getString("button" + i);
            buttons[i].setText(text);
            if (text.equals("X")) {
                buttons[i].setTextColor(ContextCompat.getColor(this, R.color.player_x));
            } else if (text.equals("O")) {
                buttons[i].setTextColor(ContextCompat.getColor(this, R.color.player_o));
            }
        }

        joueurTextView.setText(joueur1Turn ? "Joueur 1" : "Joueur 2");
        resultTextView.setText(savedInstanceState.getString("resultText"));
        resultTextView.setTextColor(savedInstanceState.getInt("resultColor", Color.BLACK));
        resultTextView.setVisibility(savedInstanceState.getInt("resultVisibility"));
    }
}