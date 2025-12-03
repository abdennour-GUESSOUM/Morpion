package com.ubo.Morpion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private Button btn3x3;
    private Button btn4x4;
    private Button btn5x5;
    private Button btnBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn3x3 = findViewById(R.id.btn3x3);
        btn4x4 = findViewById(R.id.btn4x4);
        btn5x5 = findViewById(R.id.btn5x5);
        btnBot = findViewById(R.id.btnBot);

        btn3x3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(3);
            }
        });

        btn4x4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(4);
            }
        });

        btn5x5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(5);
            }
        });

        btnBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, BotGameActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startGame(int gridSize) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("GRID_SIZE", gridSize);
        startActivity(intent);
    }
}