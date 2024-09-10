import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 600;
    int boardHeight = 800;

    Image bgImg;
    Image birdImg;
    Image topTubeImg;
    Image bottomTubeImg;

    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 55;
    int birdHeight = 50;

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

    int tubeX = boardWidth;
    int tubeY = 0;
    int tubeWidth = 100;
    int tubeHeight = 640;
    int openingSpace = boardHeight / 4;
    JButton restartButton;


    class Tube{
        int x = tubeX;
        int y = tubeY;
        int width = tubeWidth;
        int height = tubeHeight;
        Image img;
        boolean passed = false;

        Tube(Image img) {
            this.img = img;
        }
    }

    Bird bird;
    int velocityX = -5;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Tube> tubes;
    Random random = new Random();

    Timer gameLoop;
    Timer placeTubeTimer;
    boolean gameOver = false;
    double score = 0;

    Image[] birdFrames;
    int currentFrame = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        bgImg = new ImageIcon(getClass().getResource("./background.png")).getImage();
        birdFrames = new Image[]{
                new ImageIcon(getClass().getResource("./bird1.png")).getImage(),
                new ImageIcon(getClass().getResource("./bird2.png")).getImage(),
                new ImageIcon(getClass().getResource("./bird3.png")).getImage()
        };
        topTubeImg = new ImageIcon(getClass().getResource("./top-tube.png")).getImage();
        bottomTubeImg = new ImageIcon(getClass().getResource("./bottom-tube.png")).getImage();

        birdImg = birdFrames[currentFrame];
        bird = new Bird(birdImg);

        tubes = new ArrayList<Tube>();

        placeTubeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeTubes();
            }
        });
        placeTubeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        // Create a restart button
        restartButton = new JButton("Restart");
        restartButton.setBounds(boardWidth / 2 - 50, boardHeight / 2 - 30, 100, 50);
        restartButton.setVisible(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        this.setLayout(null);
        this.add(restartButton); // Add button to the panel
    }


    void placeTubes() {
        int randomTubeY = (int) (tubeY - tubeHeight/4 - Math.random()*(tubeHeight/2));

        Tube topTube = new Tube(topTubeImg);
        topTube.y = randomTubeY;
        tubes.add(topTube);

        Tube bottomTube = new Tube(bottomTubeImg);
        bottomTube.y = topTube.y + tubeHeight + openingSpace;
        tubes.add(bottomTube);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    int animationCounter = 0;
    public void draw(Graphics g) {
        // Bg
        g.drawImage(bgImg, 0, 0, boardWidth, boardHeight, null);
        // Bird Animation
        animationCounter++;
        if (animationCounter % 5 == 0) { // Change the frame every 5 game ticks
            currentFrame = (currentFrame + 1) % birdFrames.length; // Cycle through frames
        }
        birdImg = birdFrames[currentFrame]; // Update the bird image for animation

        // Draw Bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        // Tubes
       for(int i = 0; i < tubes.size(); i++) {
           Tube tube = tubes.get(i);
           g.drawImage(tube.img, tube.x, tube.y, tube.width, tube.height, null);
       }

       g.setColor(Color.white);

       g.setFont(new Font("Arial", Font.BOLD, 32));

       if(gameOver) {
           g.drawString("Game Over! Score " + String.valueOf((int) score), 150, 350);
           restartButton.setVisible(true);
       }
       else {
           g.drawString("Score: "  + String.valueOf((int) score), 15, 35);
       }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < tubes.size(); i++) {
            Tube tube = tubes.get(i);
            tube.x += velocityX;

            if(!tube.passed && bird.x > tube.x + tube.width) {
                score += 0.5;
                tube.passed = true;
            }

            if (collision(bird, tube)) {
                gameOver = true;
            }

            if(bird.y > boardHeight) {
                gameOver = true;
            }
        }
    }

    boolean collision(Bird a, Tube b) {
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
            placeTubeTimer.stop();
            gameLoop.stop();
        }
    }

    void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        tubes.clear();
        gameOver = false;
        score = 0;
        restartButton.setVisible(false);
        gameLoop.start();
        placeTubeTimer.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_UP) {
            velocityY = -9;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

        }

        if(gameOver) {
            bird.y = birdY;
            velocityY = 0;
            tubes.clear();
            if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                restartGame();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
