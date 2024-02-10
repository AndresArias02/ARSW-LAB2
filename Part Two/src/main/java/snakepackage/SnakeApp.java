package snakepackage;

import java.awt.*;

import javax.swing.*;

import enums.GridSize;

import java.awt.Graphics;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private static JFrame frame;

    private JMenuBar jMenuBar;

    private JMenu options;
    private static Board board;

    private Object lock = new Object();
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];

    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();

        frame.add(board,BorderLayout.CENTER);
        prepareActionsMenu();
    }

    public void prepareActionsMenu(){

        JPanel actionsBPabel=new JPanel();
        GridLayout gridLayout = new GridLayout(1,3);

        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            start();
            start.setEnabled(false);
        });

        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> stop());

        JButton resume = new JButton("Resume");
        resume.addActionListener(e -> resume());

        actionsBPabel.setLayout(gridLayout);
        actionsBPabel.add(start);
        actionsBPabel.add(stop);
        actionsBPabel.add(resume);

        frame.add(actionsBPabel,BorderLayout.SOUTH);
    }


    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {

        for (int i = 0; i != MAX_THREADS; i++) {

            snakes[i] = new Snake(i + 1, spawn[i], i + 1,lock);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            //thread[i].start();
        }

        frame.setVisible(true);

        while (true) {
            int x = 0;
            for (int i = 0; i != MAX_THREADS; i++) {
                if (snakes[i].isSnakeEnd() == true) {
                    x++;
                }
            }
            if (x == MAX_THREADS) {
                break;
            }
        }

        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("["+i+"] :"+thread[i].getState());
        }

    }

    public void stop(){
        int snakeLenght = 0;
        Snake largestSnake = null;
        for(Snake s: snakes){
            s.setStop(true);
            int actualSnake = s.getBody().size();
            if( actualSnake > snakeLenght){
                snakeLenght = actualSnake;
                largestSnake = s;
            }
        }
        board.largestSnake = largestSnake;
        board.setStop(true);
        board.repaint();
    }

    public void resume(){
        synchronized (lock){
            for(Snake s: snakes){
                s.setStop(false);
            }
            lock.notifyAll();
        }
        board.setStop(false);
    }

    public void start(){
        for(Thread t: thread){
            t.start();
        }
    }

    public static SnakeApp getApp() {
        return app;
    }

}
