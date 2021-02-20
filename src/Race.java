//Race Car Game

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.Timer;
import java.util.Vector;
import java.util.Random;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Race {
    private static Boolean endgame;
    private static BufferedImage background;
    private static BufferedImage track;
    private static BufferedImage gameOver;
    private static BufferedImage player1;
    private static BufferedImage player2;

    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;
    private static Boolean wasdUpPressed;
    private static Boolean wasdDownPressed;
    private static Boolean wasdLeftPressed;
    private static Boolean wasdRightPressed;
    private static ImageObject p1;
    private static ImageObject p2;
    private static ImageObject trackBorder;
    private static ImageObject middleTrackBorder;
    private static ImageObject finishLine;
    private static double playerWidth;
    private static double playerHeight;
    private static double p1originalX;
    private static double p1originalY;
    private static double p2originalX;
    private static double p2originalY;
    private static double p1velocity;
    private static double p2velocity;
    private static int XOFFSET;
    private static int TRACKXOFFSET;
    private static int YOFFSET;
    private static int TRACKYOFFSET;
    private static int WINWIDTH;
    private static int WINHEIGHT;
    private static int p1LapsLeft = 3;
    private static int p2LapsLeft = 3;
    private static int timeLeft;
    private static double pi;
    private static double twoPi;
    private static JFrame appFrame;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    public static void main(String[] args) {
        setup();
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(520, 550);
        JPanel myPanel = new JPanel();

        String[] laps = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

        JComboBox<String> lapsBox = new JComboBox<>(laps);
        lapsBox.setSelectedIndex(2);
        lapsBox.addActionListener(new LapSetter());
        myPanel.add(lapsBox);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(new StartGame());
        myPanel.add(newGameButton);

        JButton quitButton = new JButton("Quit Game");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        bindKey(myPanel, "W");
        bindKey(myPanel, "S");
        bindKey(myPanel, "A");
        bindKey(myPanel, "D");

        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }


    public Race() {
        setup();
    }


    public static void setup() {
        //228 X 388
        appFrame = new JFrame("Race Game");
        XOFFSET = 10;
        TRACKXOFFSET = 160;
        YOFFSET = 10;
        TRACKYOFFSET = 125;
        WINWIDTH = 510;
        WINHEIGHT = 510;
        pi = 3.14159265358979;
        twoPi = 2.0 * 3.14159265358979;
        endgame = false;
        playerWidth = 20;
        playerHeight = 20;
        p1originalX = (double) XOFFSET + ((double) WINWIDTH / 2.0) - (playerWidth / 2.0) - 75;
        p1originalY = (double) YOFFSET + ((double) WINHEIGHT / 2.0) - (playerHeight / 2.0) + 80;
        p2originalX = (double) XOFFSET + ((double) WINHEIGHT / 2.0) - (playerWidth / 2.0) - 30;
        p2originalY = (double) YOFFSET + ((double) WINHEIGHT / 2.0) - (playerHeight / 2.0) + 80;

        try {
            player1 = ImageIO.read(new File("src/pics/player1.png"));
            player2 = ImageIO.read(new File("src/pics/player2.png"));
            background = ImageIO.read(new File("src/pics/backgroundv2.png"));
            track = ImageIO.read(new File("src/pics/trackv2.png"));
            gameOver = ImageIO.read(new File("src/pics/gameOver.png"));
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    private static
    class Animate implements Runnable {
        public void run() {
            Graphics g = appFrame.getGraphics();
            backgroundDraw();
            while (!endgame) {
//                backgroundDraw();
                trackDraw();
                playerDraw(player1, p1);
                playerDraw(player2, p2);
                g.setColor(Color.BLUE);
                g.fillRect(10,510,150,50);
                g.fillRect(10,460,150,50);
                g.fillRect(375,510,100,50);
                g.setColor(Color.PINK);
                g.drawString("Time Left: " + timeLeft, 380, 525);
                g.drawString("P1 laps: " + p1LapsLeft, 15,525);
                g.drawString("P2 laps: " + p2LapsLeft, 85,525);
                g.drawString("P1 speed: " + Math.round(p1velocity * 100), 15,475);
                g.drawString("P2 speed: " + Math.round(p2velocity * 100), 15,495);



                try {
                    Thread.sleep(32);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));

    }

    private static class KeyPressed extends AbstractAction {
        private String action;

        public KeyPressed() {
            action = "";
        }

        public KeyPressed(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {

            if (action.equals("UP")) {
                upPressed = true;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
            }
            if (action.equals("W")) {
                wasdUpPressed = true;
            }
            if (action.equals("S")) {
                wasdDownPressed = true;
            }
            if (action.equals("A")) {
                wasdLeftPressed = true;
            }
            if (action.equals("D")) {
                wasdRightPressed = true;
            }
        }
    }

    private static class KeyReleased extends AbstractAction {
        public KeyReleased() {
            action = "";
        }

        public KeyReleased(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }
            if (action.equals("W")) {
                wasdUpPressed = false;
            }
            if (action.equals("S")) {
                wasdDownPressed = false;
            }
            if (action.equals("A")) {
                wasdLeftPressed = false;
            }
            if (action.equals("D")) {
                wasdRightPressed = false;
            }
        }

        private String action;
    }

    private static class Player1Mover implements Runnable {

        private double velocitystep;
        private double rotatestep;

        public Player1Mover() {
            velocitystep = 0.01;
            rotatestep = 0.01;
        }

        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
                if (upPressed) {
                    if (p1velocity < 0) {
                        p1velocity += 5 * velocitystep;
                    } else {
                        p1velocity += velocitystep;
                    }
                } else if (downPressed) {
                    if (p1velocity >= 0) {
                        p1velocity -= 5 * velocitystep;
                    } else {
                        p1velocity -= velocitystep;
                    }
                } else {
                    if (p1velocity > 0) {
                        p1velocity -= velocitystep;
                    } else if(p1velocity < 0){
                        p1velocity += velocitystep;
                    }
                }
                if (leftPressed) {
                    if (p1velocity < 0) {
                        p1.rotate(-rotatestep);
                    } else {
                        p1.rotate(rotatestep);
                    }
                } else if (rightPressed) {
                    if (p1velocity < 0) {
                        p1.rotate(rotatestep);
                    } else {
                        p1.rotate(-rotatestep);
                    }
                } else {
                    p1.rotate(0.0);
                }
                p1.move(-p1velocity * Math.cos(p1.getAngle() - pi / 2.0), p1velocity * Math.sin(p1.getAngle() - pi / 2.0));
                p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
            }
        }
    }

    static class Player2Mover implements Runnable {
        private double velocitystep;
        private double rotatestep;

        public Player2Mover() {
            velocitystep = 0.01;
            rotatestep = 0.01;
        }

        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
                if (wasdUpPressed) {
                    if (p2velocity < 0) {
                        p2velocity += 5 * velocitystep;
                    } else {
                        p2velocity += velocitystep;
                    }
                } else if (wasdDownPressed) {
                    if (p2velocity >= 0) {
                        p2velocity -= 5 * velocitystep;
                    } else {
                        p2velocity -= velocitystep;
                    }
                } else {
                    if (p2velocity > 0) {
                        p2velocity -= velocitystep;
                    } else if(p2velocity < 0){
                        p2velocity += velocitystep;
                    }
                }
                if (wasdLeftPressed) {
                    if (p2velocity < 0) {
                        p2.rotate(-rotatestep);
                    } else {
                        p2.rotate(rotatestep);
                    }
                } else if (wasdRightPressed) {
                    if (p2velocity < 0) {
                        p2.rotate(rotatestep);
                    } else {
                        p2.rotate(-rotatestep);
                    }
                } else {
                    p2.rotate(0.0);
                }
                p2.move(-p2velocity * Math.cos(p2.getAngle() - pi / 2.0), p2velocity * Math.sin(p2.getAngle() - pi / 2.0));
                p2.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);
            }
        }
    }

    private static void playerDraw(BufferedImage player, ImageObject p) {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(rotateImageObject(p).filter(player, null), (int) (p.getX() + 0.5),
                (int) (p.getY() + 0.5), null);
    }


    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            wasdUpPressed = false;
            wasdDownPressed = false;
            wasdLeftPressed = false;
            wasdRightPressed = false;
            p1 = new ImageObject(p1originalX, p1originalY, playerWidth, playerHeight, 0.0);
            p2 = new ImageObject(p2originalX, p2originalY, playerWidth, playerHeight, 0.0);
            //160, 125, 220, 380
            trackBorder = new ImageObject(180, 145, 180, 340, 0.0);
            middleTrackBorder = new ImageObject(260, 225, 20, 180, 0.0);
            finishLine = new ImageObject(160, 335, 100, 10, 0.0);

            timeLeft = 200;

            p1velocity = 0.0;
            p2velocity = 0.0;
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {

            }
            endgame = false;
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new Player1Mover());
            Thread t3 = new Thread(new Player2Mover());
            Thread t4 = new Thread(new CollisionChecker());
            Thread t5 = new Thread(new P1FinishLineChecker());
            Thread t6 = new Thread(new P2FinishLineChecker());
            Thread t7 = new Thread(new TimeAndLapCounter());
            Thread t8 = new Thread(new AudioRunner());
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
            t6.start();
            t7.start();
            t8.start();
        }
    }

    private static class LapSetter implements ActionListener {
        private static void setLaps(int input) {
            p1LapsLeft = input;
            p2LapsLeft = input;
        }

        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            int numLaps = Integer.parseInt((String) cb.getSelectedItem());
            setLaps(numLaps);
        }
    }


    private static class CollisionChecker implements Runnable {
        public void run() {
            Random randomNumbers = new Random(LocalTime.now().getNano());
            while (!endgame) {
                try {
                    //check cars against each other
                    if (collisionOccurs(p1, p2)) {
                        p1velocity = -p1velocity;
                        p2velocity = -p2velocity;
                    }
                    if (!collisionOccurs(p1, trackBorder)) {
                        p1velocity = -p1velocity;
                    }
                    if (!collisionOccurs(p2, trackBorder)) {
                        p2velocity = -p2velocity;
                    }
                    if (collisionOccurs(p1, middleTrackBorder)) {
                        p1velocity = -p1velocity;
                    }
                    if (collisionOccurs(p2, middleTrackBorder)) {
                        p2velocity = -p2velocity;
                    }
                } catch (java.lang.ArrayIndexOutOfBoundsException jlaioobe) {

                }
            }
        }
    }

    private static Boolean collisionOccursCoordinates(double p1x1, double p1y1, double p1x2, double p1y2,
                                                      double p2x1, double p2y1, double p2x2, double p2y2) {
        if (isInside(p1x1, p1y1, p2x1, p2y1, p2x2, p2y2)) {
            return true;
        }
        if (isInside(p1x1, p1y2, p2x1, p2y1, p2x2, p2y2)) {
            return true;
        }
        if (isInside(p1x2, p1y1, p2x1, p2y1, p2x2, p2y2)) {
            return true;
        }
        if (isInside(p1x2, p1y2, p2x1, p2y1, p2x2, p2y2)) {
            return true;
        }
        if (isInside(p2x1, p2y1, p1x1, p1y1, p1x2, p1y2)) {
            return true;
        }
        if (isInside(p2x1, p2y2, p1x1, p1y1, p1x2, p1y2)) {
            return true;
        }
        if (isInside(p2x2, p2y1, p1x1, p1y1, p1x2, p1y2)) {
            return true;
        }
        if (isInside(p2x2, p2y2, p1x1, p1y1, p1x2, p1y2)) {
            return true;
        }
        return false;
    }

    private static Boolean isInside(double p1x, double p1y, double p2x1, double p2y1, double p2x2, double p2y2) {
        if ((p1x > p2x1 && p1x < p2x2) || (p1x > p2x2 && p1x < p2x1)) {
            if ((p1y > p2y1 && p1y < p2y2) || (p1y > p2y2 && p1y < p2y1)) {
                return true;
            }
        }
        return false;
    }

    private static Boolean collisionOccurs(ImageObject obj1, ImageObject obj2) {
        return collisionOccursCoordinates(obj1.getX(), obj1.getY(), obj1.getX() + obj1.getWidth(),
                obj1.getY() + obj1.getHeight(), obj2.getX(), obj2.getY(),
                obj2.getX() + obj2.getWidth(), obj2.getY() + obj2.getHeight());
    }

    private static class P1FinishLineChecker implements Runnable {
        public void run() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
            }
            while (!endgame) {
                if (collisionOccurs(p1, finishLine)) {
                    p1LapsLeft--;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    private static class P2FinishLineChecker implements Runnable {
        public void run() {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
            }
            while (!endgame) {
                if (collisionOccurs(p2, finishLine)) {
                    p2LapsLeft--;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }


    private static class TimeAndLapCounter implements Runnable {
        public void run() {
            while (!endgame) {
                if (timeLeft <= 0 || (p2LapsLeft == 0 || p1LapsLeft == 0)) {
                    endgame = true;
                } else {
                    try {
                        timeLeft--;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            }
            System.out.println("Game_Over. _You_Win!");
            gameOverDraw();
            p1LapsLeft = 3;
            p2LapsLeft = 3;
        }
    }


    private static class AudioRunner implements Runnable {
        public void run() {
            try {
                String filePath = "src/music/Kirby's Return to Dream Land Title Theme 8 Bit Remix-HQ.WAV";
                String endingFilePath = "src/music/Great Landing - Pilotwings-HQ.WAV";
                AudioInputStream audioInputStream =
                        AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
                while (!endgame) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {

                    }
                }
                clip.stop();
                AudioInputStream finalAudioInputStream =
                        AudioSystem.getAudioInputStream(new File(endingFilePath).getAbsoluteFile());
                Clip newClip = AudioSystem.getClip();
                newClip.open(finalAudioInputStream);
                newClip.loop(Clip.LOOP_CONTINUOUSLY);
                newClip.start();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
                newClip.stop();
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                System.out.println(e);
            }
        }
    }


    //Pretty sure we dont need these  -  A

    //Dont know why this is important
    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(),
                obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
        }
    }

    private static void backgroundDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(background, XOFFSET, YOFFSET, null);
    }

    private static void gameOverDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(gameOver, 60, 110, null);
    }

    //to be used eventually
    private static void trackDraw() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2D = (Graphics2D) g;
        g2D.drawImage(track, TRACKXOFFSET, TRACKYOFFSET, null);
    }


    /**
     * stopped at line 582 of our asteroids code
     */


    private static class ImageObject {
        private double x;
        private double y;
        private double xwidth;
        private double yheight;
        private double angle; // in Radians
        private double internalangle; // in Radians
        private Vector<Double> coords;
        private Vector<Double> triangles;
        private double comX;
        private double comY;

        public ImageObject() {

        }

        public ImageObject(double xinput, double yinput, double xwidthinput,
                           double yheightinput, double angleinput) {
            x = xinput;
            y = yinput;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return xwidth;
        }

        public double getHeight() {
            return yheight;
        }

        public double getAngle() {
            return angle;
        }

        public double getInternalAngle() {
            return internalangle;
        }

        public void setAngle(double angleinput) {
            angle = angleinput;
        }

        public void setInternalAngle(double internalangleinput) {
            internalangle = internalangleinput;
        }

        public Vector<Double> getCoords() {
            return coords;
        }

        public void setCoords(Vector<Double> coordsinput) {
            coords = coordsinput;
            generateTriangles();
            //printTriangles();
        }

        public void generateTriangles() {
            triangles = new Vector<Double>();
            // format: (0,1), (2,3), (4,5) is the (x,y) coords of a triangle

            // get center point of all coordinates.
            comX = getComX();
            comY = getComY();

            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i + 1));

                triangles.addElement(coords.elementAt((i + 2) % coords.size()));
                triangles.addElement(coords.elementAt((i + 3) % coords.size()));

                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }

        public void printTriangles() {
            for (int i = 0; i < triangles.size(); i = i + 6) {
                System.out.println("p0x: " + triangles.elementAt(i) + ", p0y: " +
                        triangles.elementAt(i + 1));

                System.out.println(" p1x: " + triangles.elementAt(i + 2) + ", p1y: " +
                        triangles.elementAt(i + 3));

                System.out.println(" p2x: " + triangles.elementAt(i + 4) + ", p2y: " +
                        triangles.elementAt(i + 5));
            }
        }

        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public void move(double xinput, double yinput) {
            x = x + xinput;
            y = y + yinput;
        }

        public void moveto(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }

        public void screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            if (x > rightEdge) {
                moveto(leftEdge, getY());
            }
            if (x < leftEdge) {
                moveto(rightEdge, getY());
            }
            if (y > bottomEdge) {
                moveto(getX(), topEdge);
            }
            if (y < topEdge) {
                moveto(getX(), bottomEdge);
            }
        }

        public void rotate(double angleinput) {
            angle = angle + angleinput;
            while (angle > twoPi) {
                angle = angle - twoPi;
            }
            while (angle < 0) {
                angle = angle + twoPi;
            }
        }

        public void spin(double internalangleinput) {
            internalangle = internalangle + internalangleinput;
            while (internalangle > twoPi) {
                internalangle = internalangle - twoPi;
            }
            while (internalangle < 0) {
                internalangle = internalangle + twoPi;
            }
        }
    }
}
