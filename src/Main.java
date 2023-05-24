import java.util.Random;

public class Main {

    
    public static void main(String[] args) {
        // NSquareSpaceHash hash = new NSquareSpaceHash();
        // long start, end;
        // start = System.currentTimeMillis();
        // for (int i = 0; i < 1000_000; i++){
        //     hash.insert(i);
        // }
        // end = System.currentTimeMillis();
        // System.out.println("insert time = " + (end - start));

        // start = System.currentTimeMillis();
        // for (int i = 0; i < 1000_000; i++){
        //     hash.search(i);
        // }
        // end = System.currentTimeMillis();
        // System.out.println("search time = " + (end - start));

        // start = System.currentTimeMillis();
        // for (int i = 0; i < 1000_000; i++){
        //     hash.delete(i);
        // }
        // end = System.currentTimeMillis();
        // System.out.println("delete time = " + (end - start));

        
        int m = (int)1e7;
        int n = (int)1e7;
        
        StaticNSpace hash = new StaticNSpace(n);
        
        int[] nums = new int[m];
        for (int i = 0; i < m; i++)
            nums[i] = i;
        
        long time = System.currentTimeMillis();
        hash.batchInsert(nums);
        time = System.currentTimeMillis() - time;

        System.out.println(time);
    }
}