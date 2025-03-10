package com.example.a15;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.CountDownTimer;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import android.widget.Toast;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridGame;
    private ArrayList<Button> buttons = new ArrayList<>();
    private ArrayList<Integer> numbers = new ArrayList<>();
    private int emptyIndex = 15;

    private TextView timerText;
    private int secondsElapsed = 0;
    private boolean isGameRunning = false;
    private CountDownTimer gameTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridGame = findViewById(R.id.grid_game);
        Button btnRestart = findViewById(R.id.btn_restart);
        timerText = findViewById(R.id.timerText); // Знаходимо таймер

        btnRestart.setOnClickListener(v -> restartGame());

        gridGame.post(this::createGameBoard);

        Button btnMenu = findViewById(R.id.btn_menu);
        btnMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_restart) {
                    restartGame();
                    return true;
                } else if (id == R.id.action_exit) {
                    finish();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }


    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }

        secondsElapsed = 0;
        isGameRunning = true;

        gameTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isGameRunning) return;
                secondsElapsed++;
                timerText.setText("Час: " + secondsElapsed + " сек");
            }

            @Override
            public void onFinish() { }
        }.start();
    }


    private void createGameBoard() {
        gridGame.removeAllViews();
        buttons.clear();
        numbers.clear();

        for (int i = 1; i <= 15; i++) {
            numbers.add(i);
        }
        numbers.add(0);

        shuffleNumbers();
        startTimer();
    }


    private void shuffleNumbers() {
//        new Handler().postDelayed(() -> {
//            do {
//                Collections.shuffle(numbers);
//            } while (!isSolvable());
//
//            emptyIndex = numbers.indexOf(0);
//            drawGameBoard(); // Викликаємо метод для малювання кнопок
//        }, 500);

        shuffleNumbersWin();
    }

    private void shuffleNumbersWin() {
        numbers.clear();

        for (int i = 1; i <= 15; i++) {
            numbers.add(i);
        }
        numbers.add(0); // Остання комірка пуста

        // Міняємо місцями 14 і 15, щоб залишився один правильний рух до перемоги
        Collections.swap(numbers, 14, 15);

        emptyIndex = numbers.indexOf(0);
        drawGameBoard();
    }


    private void drawGameBoard() {
        gridGame.removeAllViews();
        buttons.clear();

        int cellSize = gridGame.getLayoutParams().width / 4; // Автоматичний розмір кнопок

        for (int i = 0; i < 16; i++) {
            Button btn = new Button(this);
            btn.setText(numbers.get(i) == 0 ? "" : String.valueOf(numbers.get(i)));
            btn.setBackgroundColor(numbers.get(i) == 0 ? Color.TRANSPARENT : Color.BLUE);
            btn.setTextSize(20);
            btn.setTextColor(Color.WHITE);
            btn.setOnClickListener(this::onTileClick);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cellSize;
            params.height = cellSize;
            gridGame.addView(btn, params);
            buttons.add(btn);
        }
    }

    private void onTileClick(View view) {
        int clickedIndex = buttons.indexOf(view);
        if (isValidMove(clickedIndex)) {
            swapTiles(clickedIndex, emptyIndex);
            updateGameBoard();
            checkGameCompletion();
        }
    }

    private boolean isValidMove(int index) {
        int row = index / 4, col = index % 4;
        int emptyRow = emptyIndex / 4, emptyCol = emptyIndex % 4;
        return Math.abs(row - emptyRow) + Math.abs(col - emptyCol) == 1;
    }

    private void swapTiles(int i, int j) {
        Collections.swap(numbers, i, j);
        emptyIndex = i;
    }

    private void updateGameBoard() {
        if (buttons.isEmpty()) return;

        for (int i = 0; i < 16; i++) {
            buttons.get(i).setText(numbers.get(i) == 0 ? "" : String.valueOf(numbers.get(i)));
            buttons.get(i).setBackgroundColor(numbers.get(i) == 0 ? Color.TRANSPARENT : Color.BLUE);
        }
    }

    private boolean isSolvable() {
        int invCount = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = i + 1; j < 15; j++) {
                if (numbers.get(i) > numbers.get(j) && numbers.get(j) != 0) {
                    invCount++;
                }
            }
        }
        return invCount % 2 == 0;
    }

    private void checkGameCompletion() {
        for (int i = 0; i < 15; i++) {
            if (numbers.get(i) != i + 1) return;
        }

        isGameRunning = false;
        gameTimer.cancel();
        Toast.makeText(this, "🎉 Вітаємо! Ви виграли за " + secondsElapsed + " сек!", Toast.LENGTH_LONG).show();
    }


    private void restartGame()
    {
        shuffleNumbers();
        startTimer();
    }
}

