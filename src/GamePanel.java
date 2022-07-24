import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1000;
    static final int SCREEN_HEIGHT = 1000;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE);
    static int delay = 50;
    final int[] x = new int [GAME_UNITS];
    final int[] y = new int [GAME_UNITS];
    int bodyParts = 3;
    int applesEaten, appleX, appleY;
    char[] directions = {'D', 'R', 'U', 'L'};
    char direction;
    int running = 2;
    Timer timer;
    Random random;
    ImageIcon icon;
    JLabel label;
    Image image;
    JButton begin;

    GamePanel() {
        random = new Random();
        direction = directions[random.nextInt(4)];                           // produces 0 --> 4
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.setLayout(new GridBagLayout());
        setLabel();
        this.add(label);
        setButton();
        this.add(begin, new GridBagConstraints());
    }
    public void setLabel() {
        icon = new ImageIcon("src/arrowkeys.png");
        image = icon.getImage();
        icon = new ImageIcon(image.getScaledInstance(215, 135, Image.SCALE_SMOOTH));
        label = new JLabel("Use Arrow Keys or WASD To Move");
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setForeground(Color.WHITE);
        label.setIcon(icon);
    }

    public void setButton() {
        begin = new JButton("Start");
        begin.addActionListener(this);
    }

    public void startGame() {
        newApple();
        running = 1;
        timer = new Timer(delay, this);
        timer.start();
        x[0] = SCREEN_WIDTH/2;
        y[0] = SCREEN_HEIGHT/2;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        if (running == 1) {
            // draw lines to create grid for units
            /*
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
             */
            // draws circle (apple)
            g.setColor(Color.red);
            g.fillOval(appleX + (UNIT_SIZE / 4), appleY + (UNIT_SIZE / 4), UNIT_SIZE / 2,
                    UNIT_SIZE / 2);

            // draws snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.ITALIC, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2,
                    g.getFont().getSize());
        } else {
            gameOver(g);
        }

    }
    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move() {
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;

        }
    }
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            if ((delay - 1) > 0) {
                delay = delay - 1;
            }
            timer.setDelay(delay);              // makes game quicker
            newApple();
        }

    }
    public void checkCollisions() {
        // check for head touching body
//        for (int i = bodyParts; i > 0; i--) {
//            if ((x[0] == x[i]) && (y[0] == y[i])) {
//                running = 0;
//                break;
//            }
//        }
        // check for head colliding with left border
        if (x[0] < 0) {
            running = 0;
        }
        // check for head colliding with right border
        if (x[0] > SCREEN_WIDTH - UNIT_SIZE) {
            running = 0;
        }
        // check for head colliding with top border
        if (y[0] < 0) {
            running = 0;
        }
        // check for head colliding with bottom border
        if (y[0] > SCREEN_HEIGHT - UNIT_SIZE) {
            running = 0;
        }
        if (running == 0) {
            timer.stop();
        }
    }
    public void gameOver(Graphics g) {
        if (running == 0) {
            // L + Ratio Text
            Font customFont = null;
            try {
                customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/celticmd.ttf")).deriveFont(75f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
            g.setColor(Color.GREEN);
            g.setFont(customFont);
            FontMetrics metricsText = getFontMetrics(g.getFont());
            g.drawString("L + RATIO", (SCREEN_WIDTH - metricsText.stringWidth("L + RATIO")) / 2,
                    (SCREEN_HEIGHT - metricsText.stringWidth("L + RATIO")) / 2);

            //show Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Comic Sans MS", Font.ITALIC, 25));
            FontMetrics metricsScore = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metricsScore.stringWidth("Score: " +
                    applesEaten)) / 2, g.getFont().getSize());

            // show GIF
            Icon icon = new ImageIcon("src/mcgregor.gif");
            JLabel label = new JLabel(icon);
            JFrame frameLoader = new JFrame();
            frameLoader.setUndecorated(true);
            frameLoader.getContentPane().add(label);
            frameLoader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frameLoader.pack();
            frameLoader.setLocationRelativeTo(null);
            frameLoader.setVisible(true);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == begin) {
            begin.setVisible(false);
            label.setVisible(false);
            startGame();
        }

        if (running == 1) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();

    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                if (direction != 'R') {
                        direction = 'L';
                    }
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                if (direction != 'L') {
                    direction = 'R';
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                if (direction != 'D') {
                    direction = 'U';
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                if (direction != 'U') {
                    direction = 'D';
                }
            }
//            switch (e.getKeyCode()) {
//                case KeyEvent.VK_LEFT:
//                    if (direction != 'R') {
//                        direction = 'L';
//                    }
//                    break;
//                case KeyEvent.VK_RIGHT:
//                    if (direction != 'L') {
//                        direction = 'R';
//                    }
//                    break;
//                case KeyEvent.VK_UP:
//                    if (direction != 'D') {
//                        direction = 'U';
//                    }
//                    break;
//                case KeyEvent.VK_DOWN:
//                    if (direction != 'U') {
//                        direction = 'D';
//                    }
//                    break;
//            }
        }
    }
}
