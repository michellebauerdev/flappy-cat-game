import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyCat extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    Image backroundImg;
    Image catImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int catX = boardWidth/8;
    int catY = boardHeight/2;
    int catWidth = 48;
    int catHeight = 34;

    class Cat {
        int x = catX;
        int y = catY;
        int width = catWidth;
        int height = catHeight;
        Image Img;

        Cat(Image img) {
            this.Img = img;
        }
    }

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

    Cat cat;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;

    double score = 0;

    FlappyCat() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));

        setFocusable(true);
        addKeyListener(this);

        backroundImg = new ImageIcon(getClass().getResource("./flappycatbg.png")).getImage();
        catImg = new ImageIcon(getClass().getResource("./flappycat.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        cat = new Cat(catImg);

        pipes = new ArrayList<Pipe>();

        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        placePipesTimer.start();

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;

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
        g.drawImage(backroundImg, 0, 0, boardWidth, boardHeight, null);

        g.drawImage(cat.Img, cat.x, cat.y, cat.width, cat.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else{
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        cat.y += velocityY;
        cat.y = Math.max(cat.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && cat.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(cat, pipe)) {
                gameOver = true;
            }
        }

        if (cat.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Cat cat, Pipe pipe) {
        return cat.x < pipe.x + pipe.width && cat.x + cat.width > pipe.x && cat.y < pipe.y + pipe.height && cat.y + cat.height > pipe.y;
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
                cat.y = catY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}