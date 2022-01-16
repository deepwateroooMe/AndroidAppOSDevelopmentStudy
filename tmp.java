import java.util.concurrent.Callable;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;
import java.util.stream.*;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toMap;

class MyTask implements Callable<Integer> {  
    private int upperBounds;  
    public MyTask(int upperBounds) {  
        this.upperBounds = upperBounds;  
    }  
    @Override  
        public Integer call() throws Exception {  
        int sum = 0;   
        for (int i = 1; i <= upperBounds; i++) 
            sum += i;  
        return sum;  
    }  
}  
  
public class tmp {  
    public static void main(String[] args) throws Exception {  
        List<Future<Integer>> list = new ArrayList<>();  
        ExecutorService service = Executors.newFixedThreadPool(10);  
        for(int i = 0; i < 10; i++) {  
            list.add(service.submit(new MyTask((int) (Math.random() * 100))));  
        }  
          
        int sum = 0;  
        for(Future<Integer> future : list) {  
            while(!future.isDone()) ;  
            sum += future.get();  
        }  
          
        System.out.println(sum);  
    }  
}  