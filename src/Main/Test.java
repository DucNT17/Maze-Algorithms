package Main;
 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
 
public class Test {
    public static void main(String args[]) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(100, "A");
        map.put(101, "B");
        map.put(102, "C");
        // show map
        Set<Integer> set = map.keySet();
        map.put(100,"D");
        System.out.print(map.get(100));
        for (int i = 100; i < 103; i++) {
        	map.remove(i);
        }
    
    System.out.print(map.get(100));
}
}
