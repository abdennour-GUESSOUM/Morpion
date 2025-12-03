package com.ubo.ocs_project_v2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        createGrid();
        initListeners();

        if (savedInstanceState != null) {
            restoreGameState(savedInstanceState);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        createGrid();
        restoreCurrentState();
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
        Button[] oldButtons = buttons;
        buttons = new Button[totalCells];

        int buttonSize = calculateButtonSize();

        for (int i = 0; i < totalCells; i++) {
            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setText("");
            button.setTextColor(Color.BLACK);
            button.setTextSize(20);
            button.setBackgroundColor(Color.LTGRAY);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            params.setMargins(4, 4, 4, 4);
            button.setLayoutParams(params);

            final int index = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCellClick(index);
                }
            });

            if (oldButtons != null && i < oldButtons.length) {
                button.setText(oldButtons[i].getText());
                if (oldButtons[i].getText().equals("X")) {
                    button.setTextColor(Color.RED);
                } else if (oldButtons[i].getText().equals("O")) {
                    button.setTextColor(Color.GREEN);
                }
            }

            buttons[i] = button;
            gridLayout.addView(button);
        }
    }

    private int calculateButtonSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int orientation = getResources().getConfiguration().orientation;
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        int availableSpace;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            availableSpace = (int) (screenHeight * 0.7);
        } else {
            availableSpace = (int) (screenWidth * 0.9);
        }

        int margin = 8;
        int buttonSize = (availableSpace - (gridSize + 1) * margin) / gridSize;

        int maxSize = 120*3;
        int minSize = 50;

        if (buttonSize > maxSize) {
            buttonSize = maxSize;
        } else if (buttonSize < minSize) {
            buttonSize = minSize;
        }

        return buttonSize;
    }

    private void restoreCurrentState() {
        joueurTextView.setText(joueur1Turn ? "Joueur 1" : "Joueur 2");
        if (jeuTermine && resultTextView != null) {
            resultTextView.setVisibility(View.VISIBLE);
        }
    }

    private void initListeners() {
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
        if (jeuTermine || !buttons[index].getText().toString().isEmpty()) {
            return;
        }

        if (joueur1Turn) {
            buttons[index].setText("X");
            buttons[index].setTextColor(Color.RED);
        } else {
            buttons[index].setText("O");
            buttons[index].setTextColor(Color.GREEN);
        }

        compteurTours++;

        if (checkVictoire()) {
            jeuTermine = true;
            String gagnant = joueur1Turn ? "Joueur 1" : "Joueur 2";
            resultTextView.setText(gagnant + " a gagné !");
            resultTextView.setVisibility(View.VISIBLE);
            return;
        }

        if (compteurTours == gridSize * gridSize) {
            jeuTermine = true;
            resultTextView.setText("Match nul !");
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
            if (checkLine(grille, i, 0, 0, 1)) {
                return true;
            }
        }

        for (int i = 0; i < gridSize; i++) {
            if (checkLine(grille, 0, i, 1, 0)) {
                return true;
            }
        }

        if (checkLine(grille, 0, 0, 1, 1)) {
            return true;
        }

        if (checkLine(grille, 0, gridSize - 1, 1, -1)) {
            return true;
        }

        return false;
    }

    private boolean checkLine(String[][] grille, int startRow, int startCol, int rowIncrement, int colIncrement) {
        String first = grille[startRow][startCol];
        if (first.isEmpty()) {
            return false;
        }

        int row = startRow;
        int col = startCol;

        for (int i = 0; i < gridSize; i++) {
            if (!grille[row][col].equals(first)) {
                return false;
            }
            row += rowIncrement;
            col += colIncrement;
        }

        return true;
    }

    private void resetGame() {
        for (Button button : buttons) {
            button.setText("");
            button.setTextColor(Color.BLACK);
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
    }

    private void restoreGameState(Bundle savedInstanceState) {
        joueur1Turn = savedInstanceState.getBoolean("joueur1Turn");
        compteurTours = savedInstanceState.getInt("compteurTours");
        jeuTermine = savedInstanceState.getBoolean("jeuTermine");

        for (int i = 0; i < buttons.length; i++) {
            String text = savedInstanceState.getString("button" + i);
            buttons[i].setText(text);
            if (text.equals("X")) {
                buttons[i].setTextColor(Color.RED);
            } else if (text.equals("O")) {
                buttons[i].setTextColor(Color.GREEN);
            }
        }

        joueurTextView.setText(joueur1Turn ? "Joueur 1" : "Joueur 2");
        resultTextView.setText(savedInstanceState.getString("resultText"));
        resultTextView.setVisibility(savedInstanceState.getInt("resultVisibility"));
    }
}