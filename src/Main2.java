import java.util.HashSet;

public class Main2 {
    public static void main(String[] args) {
        HashSet<Integer> set = new HashSet<>();
        
        long time = System.currentTimeMillis();
        for (int i = 0; i < 1e9; i++) {
            set.add(i);
        }

        Math.log1p(time)
        time = System.currentTimeMillis() - time;

        System.out.println(time);
    }
}
