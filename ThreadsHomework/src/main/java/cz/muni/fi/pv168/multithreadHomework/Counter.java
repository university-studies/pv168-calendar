package cz.muni.fi.pv168.multithreadHomework;

/**
 * Created by Mario Kudolani on 14.4.2014.
 */
public class Counter implements Runnable {

    private static int number = 0;
    private final static Object LOCK = new Object();
    private String name;

    public Counter(){
        name = "";
    }

    public Counter(String name){
        this.name = name;
    }

    @Override
    public void run() {

        while(number < 50){
            for(long i = 0;i<100000000;i++);
            synchronized (LOCK){
                for(long i = 0;i<100000000;i++);
                if(number <= 50)
                    System.out.println(name + ": " +number++);
            }
            for(long i = 0;i<1000000;i++);
        }

        /*
        for(int i = 0; number <= 50; i++){
            System.out.println(name + ": " +number);
            synchronized (LOCK) {
                //if(number < 50)
                    number++;
            }
        }
        */
    }

    public String getName() {
        return name;
    }


}
