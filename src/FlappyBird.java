import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Objects;



import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    //Game Logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;

    Timer gameLoop;
    Timer placePipesTimer;
    boolean isPaused = false;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //Loading Images
        backgroundImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("res/flappybirdbg.png"))).getImage();
        birdImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("res/flappybird.png"))).getImage();
        topPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("res/toppipe.png"))).getImage();
        bottomPipeImg = new ImageIcon(Objects.requireNonNull(getClass().getResource("res/bottompipe.png"))).getImage();

        //Bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        //Pipe Placing Timer
        placePipesTimer = new Timer(1500, e -> placePipes());
        placePipesTimer.start();

        //Game Timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - (double) pipeHeight / 4 - Math.random() * ((double) pipeHeight / 2));
        int openingSpace = boardWidth / 3;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("ARIAL", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("GAME OVER: " + (int) score, boardWidth/2, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), boardWidth/2, 35);
        }

        // Pause message
        if (isPaused) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("ARIAL", Font.BOLD, 48));
            g.drawString("PAUSED", boardWidth/4, boardHeight/4);
        }
    }

    public void move() {
        //Bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);
        //Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // Not one because it passes 2 pipes
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            isPaused = !isPaused;
            if (isPaused) {
                gameLoop.stop();
                placePipesTimer.stop();
            } else {
                gameLoop.start();
                placePipesTimer.start();
            }
            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
