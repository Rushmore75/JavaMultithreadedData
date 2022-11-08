import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class app {

    public static final String outFileDir = "./outputs";

    public static void main(String[] args) throws Exception {
        // read file into list of strings
        // send parts of lists to each new thread.

        BufferedReader in = new BufferedReader(new FileReader("./words.txt"));
        List<String> al = new ArrayList<>();
        in.lines().forEach(line -> {
            al.add(line);
        });
        for (int i = 0; i < 4; i++) {
            test(al, (outFileDir+"/output_"+ i+".csv"));    
        }
        
        // it may take a bit to close 
        in.close();
    }

    public static void test(List<String> list, String outputFile) {
        final int threads = 1024;

        class Result {
            public Long elapsedTime;
            public Map<Integer, Integer> result;
            public int threadCount;

            public Result(Long elapsedTime, Map<Integer, Integer> result, int threadCount) {
                this.elapsedTime = elapsedTime;
                this.result = result;
                this.threadCount = threadCount;
            }
        }
        
        List<Result> al = new ArrayList<>();
        
        // Test cases
        for (int i = 1; i <= threads; i++) {

            // timestamp and run
            long instant = System.currentTimeMillis();
            var result = workPool(list, i);
            instant = System.currentTimeMillis() - instant;
            al.add(new Result(instant, result, i));

        }
        
        new File(outputFile).getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(outputFile)) {
            
            // List results of tests
            Result slowest = null;
            Result fastest = null;
            for (Result result : al) {
                String res = "Threads," + result.threadCount + ", Time," + result.elapsedTime + "\n";
                writer.write(res);
                // System.out.print(res); // used to print the svg file to std::out
                // find slowest
                if (slowest == null) { slowest = result; } 
                    else if (result.elapsedTime > slowest.elapsedTime) {
                        slowest = result;
                    }
                // find fastest
                if (fastest == null) { fastest = result; }
                    else if (result.elapsedTime < fastest.elapsedTime) {
                        fastest = result;
                    }
            }

            writer.close();

            System.out.println(
                "Fastest: " + fastest.threadCount
                + " Speed: " + fastest.elapsedTime
                + "ms"
            );
            System.out.println(
                "Slowest: " + slowest.threadCount
                + " Speed: " + slowest.elapsedTime
                + "ms");
        } catch (IOException e) { e.printStackTrace(); }

        // see if results are the same
        for (int i = 0; i < al.size()-1; i++) {

            var res1 = al.get(i).result;
            var res2 = al.get(i+1).result;
            for (var item : res1.keySet()) {
                Integer x = res1.get(item);
                Integer y = res2.get(item);

                if (!x.equals(y)) {
                    System.out.print(Character.toString(item)+": "+x+":"+y);
                }
            }
        }
    }
    
    public static Map<Integer, Integer> workPool(List<String> list, int threads) {

        class Tuple {
            Thread t;
            Counter c;
            Tuple(Counter c, Thread t) {
                this.t =t;
                this.c =c;
            }
        }

        int size = list.size() / threads;
        List<Tuple> futures = new ArrayList<>();

        // create pool and distribute work
        for (int i = 0; i < threads; i++) {

            int startIndex = i*size;
            int endIndex = (i*size)+size;

            // If it is the last split, make it go to the end of the list.
            // This fixes issues where the last few elements wouldn't get
            // selected for any thread.
            if (i == threads-1) {
                endIndex = list.size();
            }
            List<String> subList = list.subList(startIndex, endIndex);
            
            Counter counter = new Counter(subList);
            Thread thread = new Thread(counter);
            thread.start();
            futures.add(new Tuple(counter, thread));
        }

        // combine results
        Map<Integer, Integer> map = new HashMap<>();
        for (Tuple tuple : futures) {
            
            try {
                // the result of the working future
                tuple.t.join();
                var fResult = tuple.c.getMap();
                // run thru the result
                fResult.keySet().forEach(key -> {
                    // get the value for each key
                    var newTotal = fResult.get(key);

                    // add to the value already there.
                    var oldTotal = map.get(key);
                    newTotal = (oldTotal == null) ? newTotal : oldTotal + newTotal;
                    map.put(key, newTotal);  
                });
            
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
        return map;
    }
}
