import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Counter implements Runnable {

    private volatile  Map<Integer, Integer> map = new Hashtable<>();
    private List<String> list;

    public Counter(List<String> list) {
        this.list = list;
    }

    public Map<Integer, Integer> getMap() {
        return map;
    }

    @Override
    public void run() {
        // add parallel and see if it speeds up.
        list.stream().forEach(line -> {
            line.toLowerCase().chars().forEach(letter -> {
                // make sure to not count spaces
                if (Character.isAlphabetic(letter)) {
                    Integer total = map.get(letter);
                    // null checks really improve the Java experience...
                    total = (total == null) ? 1 : total + 1;
                    map.put(letter, total);  
                }
            });
        });
    }
}
