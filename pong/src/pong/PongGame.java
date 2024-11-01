package pong;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PongGame extends JPanel implements ActionListener {
    private int ballX = 100, ballY = 100, ballDX = 2, ballDY = 2;
    private int paddleX = 200, paddleY = 450, paddleWidth = 60, paddleHeight = 10;
    private int score = 0;
    private int highScore = 0; // Mejor puntaje
    private int level = 1;
    private int speedIncrease = 1;
    private boolean levelUp = false; // Para mostrar el mensaje de subida de nivel
    private boolean gameOver = false; // Para controlar el estado de juego

    private static final String HIGH_SCORE_FILE = "highscore.txt"; // Archivo para guardar el mejor puntaje

    public PongGame() {
        loadHighScore(); // Cargar el mejor puntaje al inicio
        Timer timer = new Timer(5, this);
        timer.start();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!gameOver) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        paddleX -= 15;
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        paddleX += 15;
                    }
                    paddleX = Math.max(0, Math.min(paddleX, getWidth() - paddleWidth));
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    resetGame(); // Reiniciar el juego al presionar espacio
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.fillOval(ballX, ballY, 15, 15);
        g.fillRect(paddleX, paddleY, paddleWidth, paddleHeight);
        
        // Mostrar puntuación, mejor puntaje y nivel
        g.drawString("Score: " + score, 10, 20);
        g.drawString("High Score: " + highScore, 10, 40);
        g.drawString("Level: " + level, 10, 60);
        if (levelUp) {
            g.drawString("¡Subiste de nivel!", 10, 80);
        }
        
        // Mostrar mensaje de pérdida
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("¡Perdiste!", getWidth() / 2 - 100, getHeight() / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Presiona ESPACIO para reiniciar", getWidth() / 2 - 150, getHeight() / 2 + 40);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            ballX += ballDX;
            ballY += ballDY;

            if (ballX < 0 || ballX > getWidth() - 15) {
                ballDX = -ballDX;
            }
            if (ballY < 0) {
                ballDY = -ballDY;
            }
            if (ballY > paddleY - 15 && ballX > paddleX && ballX < paddleX + paddleWidth) {
                ballDY = -ballDY;
                score++; // Aumentar la puntuación
                levelUp = true; // Indicar que se subió de nivel

                // Aumentar nivel y velocidad
                if (score % 5 == 0) { // Aumentar de nivel cada 5 puntos
                    level++;
                    ballDX += (ballDX > 0) ? speedIncrease : -speedIncrease;
                    ballDY += (ballDY > 0) ? speedIncrease : -speedIncrease;
                }
            }
            if (ballY > getHeight()) {
                gameOver = true; // Establecer el estado de juego a perdido
                updateHighScore(); // Actualizar el mejor puntaje
            }

            // Reiniciar el mensaje de subida de nivel después de un tiempo
            if (levelUp) {
                Timer levelUpTimer = new Timer(1000, evt -> {
                    levelUp = false;
                    ((Timer) evt.getSource()).stop();
                });
                levelUpTimer.setRepeats(false);
                levelUpTimer.start();
            }

            repaint();
        }
    }

    private void resetGame() {
        ballX = 100;
        ballY = 100;
        ballDX = 2;
        ballDY = 2;
        paddleX = 200;
        score = 0;
        level = 1;
        levelUp = false;
        gameOver = false;
        repaint(); // Redibujar la pantalla
    }

    private void loadHighScore() {
        try {
            highScore = Integer.parseInt(new String(Files.readAllBytes(Paths.get(HIGH_SCORE_FILE))).trim());
        } catch (IOException | NumberFormatException e) {
            highScore = 0; // Si no se puede leer el archivo, el mejor puntaje es 0
        }
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score; // Actualizar el mejor puntaje
            try {
                Files.write(Paths.get(HIGH_SCORE_FILE), String.valueOf(highScore).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pong Game");
        PongGame pongGame = new PongGame();
        frame.add(pongGame);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}