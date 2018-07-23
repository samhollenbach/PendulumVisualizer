import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.util.Random;

/**
 * Created by samhollenbach on 12/8/14.
 */
public class Pendulum extends JPanel implements MouseListener, ActionListener {

    JFrame f = new JFrame("Display");
    boolean isRunning = true;
    static int fps = 30;
    Insets insets;
    BufferedImage backBuffer;
    static int boxesX = 15;
    static int boxesY = 15;
    int widthX, widthY;
    GraphicsDevice dev;

    private final int NUM_BALL = 100;
    private final int ballSize = 15;
    private int[] ballsY = new int[NUM_BALL];
    private double timeStep = 0;
    private final int BALL_HEIGHT = 300;
    private int timeCount = 0;
    private double scale = 2;
    private boolean up = true;

    private double l1I = 0.;
    private double l2I = 0.;
    private double l3I = 0.;
    private double l4I = 0.;

    JButton pend;
    JButton tree;
    JButton lung;
    JButton exit;

    private int colorR = 100;
    private int colorG = 0;
    private int colorB = 200;
    private boolean upR = true;
    private boolean upG = true;
    private boolean upB = true;


    private int program = 0;

    public Pendulum(){

    }

    public static void main(String[] args) {
        Pendulum sg = new Pendulum();
        sg.run();
    }

    void update(){
    }

    public void switchDisplays(){

        if(program == 0){
            exit.setVisible(true);
            pend.setVisible(true);
            tree.setVisible(true);
//            // Transparent 16 x 16 pixel cursor image.
//            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
//
//            // Create a new blank cursor.
//            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
//                    cursorImg, new java.awt.Point(0, 0), "blank cursor");
//
//            // Set the blank cursor to the JFrame.
//            f.getContentPane().setCursor(blankCursor);

        }else{
            exit.setVisible(false);
            pend.setVisible(false);
            tree.setVisible(false);
        }

    }

    public void run(){
        initialize();
        while(isRunning)
        {

            long time = System.currentTimeMillis();

            update();
            draw();

            //  delay for each frame  -   time it took for one frame
            time = (1000 / fps) - (System.currentTimeMillis() - time);
            //System.out.print(time+"\n");
            if (time > 0)
            {
                try
                {
                    Thread.sleep(time);
                }
                catch(Exception e){}
            }
        }

        setVisible(false);
        System.exit(0);


    }

    void initialize(){

        GraphicsEnvironment env =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        dev = env.getDefaultScreenDevice();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBackground(Color.BLACK);
        f.setResizable(false);
        f.setUndecorated(true);
        f.add(this);
        f.pack();
        dev.setFullScreenWindow(f);
        f.addMouseListener(this);



        exit = new JButton("exit");
        pend = new JButton("pendulum");
        tree = new JButton("tree");
        lung = new JButton("lung");

        exit.setVisible(true);
        pend.setVisible(true);
        tree.setVisible(true);
        lung.setVisible(true);

        add(exit);
        add(pend);
        add(tree);
        add(lung);

        exit.addActionListener(this);
        pend.addActionListener(this);
        tree.addActionListener(this);
        lung.addActionListener(this);

        f.setVisible(true);

        //double buffer initialization
        backBuffer = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);

        widthX = (int)(f.getWidth() / boxesX)-1;
        widthY = (int)(f.getHeight() / boxesY)-1;


    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(exit)){
            System.exit(0);
        }else if(e.getSource().equals(pend)){
            program = 1;
            switchDisplays();
        }else if(e.getSource().equals(tree)){
            program = 2;
            switchDisplays();
        }else if(e.getSource().equals(lung)){
            program = 3;
            switchDisplays();
        }
    }



    private void incrementBackground(){

        int incrementR = 5;
        int incrementG = 6;
        int incrementB = 7;


        if(upR){
            colorR += incrementR;
        }else{
            colorR -= incrementR;
        }
        if(upG){
            colorG += incrementG;
        }else{
            colorG -= incrementG;
        }
        if(upB){
            colorB += incrementB;
        }else{
            colorB -= incrementB;
        }

        if(colorR > 255){
            colorR = 255;
        }else if(colorR < 0){
            colorR = 0;
        }
        if(colorG > 255){
            colorG = 255;
        }else if(colorG < 0){
            colorG = 0;
        }
        if(colorB > 255){
            colorB = 255;
        }else if(colorB < 0){
            colorB = 0;
        }

        if(colorR == 0 || colorR == 255){
            upR = !upR;
        }
        if(colorG == 0 || colorG == 255){
            upG = !upG;
        }
        if(colorR == 0 || colorR == 255){
            upB = !upB;
        }

    }

    void draw(){
        timeCount++;
        if(timeCount == fps){
            timeCount = 0;
        }
        Graphics g = getGraphics();
        //double buffer graphics object
        Graphics bbg = backBuffer.getGraphics();


        if(program != 0){
            Color backgroundColor = new Color(255-colorG,255-colorB,255-colorR);

            bbg.setColor(backgroundColor);
            bbg.fillRect(0, 0, f.getWidth(), f.getHeight());
        }


        if(program == 1) {
            moveBalls(bbg);
            incrementBackground();
        }
        if (program == 2){
            tree(bbg, 9, 200, f.getWidth() / 2, (int) (f.getHeight() / 1.2), 0);
            incrementBackground();
        }
        if (program == 3){
            lungs(bbg, 5, 200, f.getWidth() / 2, (int) (f.getHeight() / 1.2), 0);
            incrementBackground();
        }


        if(program != 0){
            g.drawImage(backBuffer, 0, 0, this);
        }

    }



    public void lungs(Graphics bbg, int n, int length, int x, int y, double angle){



        //angle modifications of the different branches
        double r1 = angle + Math.toRadians(-45);
        double r2 = angle + Math.toRadians(45);
        double r3 = angle + Math.toRadians(135);
        double r4 = angle + Math.toRadians(-135);

        //length modifications of the different branches
        double l1 = length/1.3 + (l1I*5);
        double l2 = length/1.3 + (l2I*5);
        double l3 = length/1.3 + (l3I*5);
        double l4 = length/1.3 + (l4I*5);

        //x and y points of the end of the different branches
        int ax = (int)(x - Math.sin(r1)*l1)+(int)(l1I);
        int ay = (int)(y - Math.cos(r1)*l1)+(int)(l1I);
        int bx = (int)(x - Math.sin(r2)*l2)+(int)(l2I);
        int by = (int)(y - Math.cos(r2)*l2)+(int)(l2I);
        int cx = (int)(x - Math.sin(r3)*l3)+(int)(l3I);
        int cy = (int)(y - Math.cos(r3)*l3)+(int)(l3I);
        int dx = (int)(x - Math.sin(r4)*l4)+(int)(l4I);
        int dy = (int)(y - Math.cos(r4)*l4)+(int)(l4I);

        if(n == 0){
            return;
        }


        increment();
        Color fluidC = new Color(colorR,colorG,colorB);
        //bbg.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random()*255)));
        bbg.setColor(fluidC);


        //draw different branches
        bbg.drawLine(x,y,ax,ay);
        bbg.drawLine(x,y,bx,by);
        bbg.drawLine(x,y,cx,cy);
        bbg.drawLine(x,y,dx,dy);



        //call recursively to draw fractal
        lungs(bbg, n - 1, (int)l1, ax,ay,r1);
        lungs(bbg, n - 1, (int)l2, bx,by,r2);
        lungs(bbg, n - 1, (int)l3, cx,cy,r3);
        lungs(bbg, n - 1, (int)l4, dx,dy,r4);


    }


    public void tree(Graphics bbg, int n, int length, int x, int y, double angle){



        //angle modifications of the different branches
        double r1 = angle + Math.toRadians(-30);
        double r2 = angle + Math.toRadians(30);
        double r3 = angle + Math.toRadians(0);

        //length modifications of the different branches
        double l1 = length/1.3 + (l1I*5);
        double l2 = length/1.3 + (l2I*5);
        double l3 = length/1.3 + (l3I*5);

        //x and y points of the end of the different branches
        int ax = (int)(x - Math.sin(r1)*l1)+(int)(l1I);
        int ay = (int)(y - Math.cos(r1)*l1)+(int)(l1I);
        int bx = (int)(x - Math.sin(r2)*l2)+(int)(l2I);
        int by = (int)(y - Math.cos(r2)*l2)+(int)(l2I);
        int cx = (int)(x - Math.sin(r3)*l3)+(int)(l3I);
        int cy = (int)(y - Math.cos(r3)*l3)+(int)(l3I);

        if(n == 0){
            return;
        }


        increment();
        Color fluidC = new Color(colorR,colorG,colorB);
        //bbg.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random()*255)));
        bbg.setColor(fluidC);


        //draw different branches
        bbg.drawLine(x,y,ax,ay);
        bbg.drawLine(x,y,bx,by);
        bbg.drawLine(x,y,cx,cy);

        //call recursively to draw fractal
        tree(bbg,n-1,(int)l1, ax,ay,r1);
        tree(bbg, n - 1, (int)l2, bx,by,r2);
        tree(bbg, n - 1, (int)l3, cx,cy,r3);


    }

    public static void fractal(Graphics bbg, int n, double length, double x, double y){

        if(n == 0){
            return;
        }
        int x0 = (int)(x - length/2);
        int x1 = (int)(x + length/2);
        int y0 = (int)(y - length*Math.sqrt(3)/4.);
        int y1 = (int)(y + length*Math.sqrt(3)/4.);

        bbg.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));

        bbg.drawLine(x0,y0,x1,y0);
        bbg.drawLine(x0,y0,(int)x,y1);
        bbg.drawLine(x1,y0,(int)x,y1);

        fractal(bbg, n-1, length/2, x0, y0);
        fractal(bbg, n-1, length/2, x1, y0);
        fractal(bbg, n-1, length/2, x+1, y1);

    }

    public void increment(){

        double increment = 0;
        if(program == 1){
            increment = 0.01;
        }else if(program == 2){
            increment = 0.001;
        }
        if(up){
            scale += increment;

            l1I += increment;
            l2I += increment;
            l3I += increment;

        }else{
            scale -= increment;

            l1I -= increment;
            l2I -= increment;
            l3I -= increment;
        }
        if(scale <= 0 || scale >= 2.5){
            up = !up;
        }
    }

    public void moveBalls(Graphics bbg){

        int c1 = (f.getWidth() - 100) / NUM_BALL;

        for(int i = 0; i < NUM_BALL; i++){


            Color fluidC = new Color(colorR,colorG,colorB);

            Color fluidFadingC = new Color(
                    (int)(colorR+((double)i/NUM_BALL)*(colorG-colorR)),
                    (int)(colorG+((double)i/NUM_BALL)*(colorB-colorG)),
                    (int)(colorB+((double)i/NUM_BALL)*(colorR-colorB)));

            Color randC = new Color(0,(int)(255*Math.random()),(int)(255*Math.random()));
            Color c = new Color(255/NUM_BALL*i,0,255-255/NUM_BALL*i);



            bbg.setColor(fluidFadingC);
            int y = getY(i,timeStep);
            int currentHeight = (int)(BALL_HEIGHT*scale);
            int relativeHeight = Math.abs(currentHeight/2 - y);

            //int currentBallSize = ballSize - (int)(15*((double)relativeHeight/currentHeight)) + (int)(8 * scale);
            int currentBallSize = ballSize + (int)(8 * scale);

            bbg.fillOval(65 + c1 * i, (f.getHeight()/2)-(int)(scale*BALL_HEIGHT/2)+y, currentBallSize, currentBallSize);
        }
        timeStep+=0.2;
        increment();
    }

    public int getY(int i, double t){
        return (int)(scale*(BALL_HEIGHT/2 * (1 + Math.sin((t * (i/500.0 + 0.02)) % 2*Math.PI))));
    }


    @Override
    public void mouseClicked(MouseEvent e) {

        if(program != 0){
            program = 0;
            switchDisplays();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
