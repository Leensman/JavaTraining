
package com.noveogroup.java;


import com.noveogroup.java.generator.POJOFactory;
import com.noveogroup.java.my_concurrency.BlockingQueue;
import com.noveogroup.java.my_concurrency.MyBlockingQueue;
import com.noveogroup.java.my_concurrency.SimpleBlockQueue;
import com.noveogroup.java.serialize.Serializer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author artem ryzhikov
 */
class Main {
    public static final int QUEUE_SIZE = 1000;
    public static final int[] COUNTS = {50000};
    public static final String[] CLASSNAMES = {"com.noveogroup.java.generator.MailMessage"};
    private static Logger log = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        final Map<String , Integer> classes = new HashMap<String , Integer>();
        for(int i=0 , limit = CLASSNAMES.length; i < limit; i++){
            classes.put(CLASSNAMES[i] , COUNTS[i]);
        }
        final String INPUT = "temp.out";
        final String OUTPUT = "temp.out";
        final String MODE = "0";
        final POJOFactory factory = new POJOFactory();
        final Stack<Object> stack = factory.gen(classes);
        final File input = new File(INPUT);
        final File output = new File(OUTPUT);
        final int mode = Integer.parseInt(MODE);
        final Serializer serializer = new Serializer(input , output);
        try{
            for (int i = 0 , limit = stack.size(); i < limit; i++) {
                serializer.store(stack.pop());
            }
        }
        catch (IOException e) {
            String message = "Wrong output";
            log.log(Level.SEVERE , message , e);
            System.out.print(message + e.getMessage());
            log.info(message + e.getMessage());

        }
        final SimpleBlockQueue<Object> queue;
        if(mode == 0){
            queue = new BlockingQueue<Object>(QUEUE_SIZE);
        } else{
            queue = new MyBlockingQueue<Object>(QUEUE_SIZE);
        }
        final AtomicBoolean flag = new AtomicBoolean(false);
        final Reader reader = new Reader(queue , serializer , flag);
        final Worker worker = new Worker(queue , flag);
        new Thread(reader).start();
        new Thread(worker).start();

    }
}