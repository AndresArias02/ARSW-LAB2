package edu.eci.arsw.primefinder;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class PrimeFinderThread extends Thread{


	int a,b;
    AtomicInteger totalPrimes;
    boolean stop;
    final Object lock;
	private List<Integer> primes;

	public PrimeFinderThread(int a, int b ,Object lock) {
		super();
                this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
        this.stop = false;
        this.lock = lock;
        this.totalPrimes = new AtomicInteger(0);
	}

        @Override
	public void run(){
            for (int i= a;i < b;i++){						
                if (isPrime(i)){
                    primes.add(i);
                    totalPrimes.incrementAndGet();
                    //System.out.println(i);
                }
                if(stop){
                    synchronized (lock){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
	}
	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

    public synchronized AtomicInteger getPrimesQuantity(){
        return totalPrimes;
    }

    synchronized void stopThread(Boolean stop){
        this.stop = stop;
    }

    synchronized void resumeThread(Boolean stop){
        this.stop = stop;
        notify();
    }


	public List<Integer> getPrimes() {
		return primes;
	}


}
