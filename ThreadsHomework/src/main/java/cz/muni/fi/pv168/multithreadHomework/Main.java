package cz.muni.fi.pv168.multithreadHomework;

/**
 * Created by Mario Kudolani on 14.4.2014.
 */
public class Main {
    public static void main(String[] args){
        Thread t1 = new Thread(new Counter("Vlakno 1"));
        Thread t2 = new Thread(new Counter("Vlakno 2"));
        Thread t3 = new Thread(new Counter("Vlakno 3"));

        t1.start();
        t2.start();
        t3.start();
    }
}
