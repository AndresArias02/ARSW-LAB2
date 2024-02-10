/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;



import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class Control extends Thread {

    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 300000;
    private final static int TMILISECONDS = 200;

    private final int NDATA = MAXVALUE / NTHREADS;

    private static Object lock = new Object();

    private PrimeFinderThread pft[];

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];
        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA, lock);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1, lock);

    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {

        for(PrimeFinderThread thread :pft){
            thread.start();
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (PrimeFinderThread thread : pft) {
                    thread.stopThread(true);
                }
                for (PrimeFinderThread thread : pft) {
                    System.out.println(
                            "El hilo: " + thread.getName() + " Encontro " + thread.getPrimesQuantity() + " Primos.");
                }
                System.out.println("Presione enter para continuar. ");
                String read;
                Scanner scanner = new Scanner(System.in);
                read = scanner.nextLine();
                if (read != null) {
                    scanner.close();
                    System.out.println("Continuando Busqueda...");
                    synchronized (lock) {
                        for (PrimeFinderThread thread : pft) {
                            thread.stopThread(false);
                        }
                        lock.notifyAll();
                    }
                }
            }
        },TMILISECONDS);

        try {
            for (PrimeFinderThread thread : pft) {
                thread.join();
            }
            for (int j = 0; j < pft.length; j++) {
                PrimeFinderThread thread = pft[j];
                System.out.println("La cantidad de numero primos encontrados por el hilo:" + thread.getName() + "fue:" + thread.getPrimesQuantity());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}



