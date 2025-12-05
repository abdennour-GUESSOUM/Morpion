package com.ubo.Morpion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
                animateButton(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGame(3);
                    }
                }, 200);
            }
        });

        btn4x4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGame(4);
                    }
                }, 200);
            }
        });

        btn5x5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGame(5);
                    }
                }, 200);
            }
        });

        btnBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(v);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MenuActivity.this, BotGameActivity.class);
                        startActivity(intent);
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

    private void startGame(int gridSize) {
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        intent.putExtra("GRID_SIZE", gridSize);
        startActivity(intent);
    }
}