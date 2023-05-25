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

        hash.insert(n + 1);
        hash.insert(n + 2);
        hash.insert(n + 3);
        hash.insert(n + 4);
        hash.insert(n + 5);
        hash.insert(n + 6);

        hash.insert(n + 7);
        hash.insert(n + 8);
        hash.insert(n + 9);
        hash.insert(n + 10);
        hash.insert(n + 11);
        hash.insert(n + 12);

        System.out.println(time);

        int errcnt = 0;
        for (int i = 0; i < m; i++)
            if (!hash.search(i))
                errcnt++;
        System.out.println(errcnt);
    }
}