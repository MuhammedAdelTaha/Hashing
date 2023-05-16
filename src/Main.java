public class Main {
    public static void main(String[] args) {
        NSquareSpaceHash hash = new NSquareSpaceHash();
        long start, end;

        start = System.currentTimeMillis();
        for (int i = 0; i < 1000_000; i++){
            hash.insert(i);
        }
        end = System.currentTimeMillis();
        System.out.println("insert time = " + (end - start));
        hash.print();

        int count = 0;
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000_000; i++){
            if(hash.search(i).getKey()) count++;
        }
        end = System.currentTimeMillis();
        System.out.println("search time = " + (end - start));
        hash.print();
        System.out.println("found : " + count);

        count = 0;
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000_000; i++){
            if(hash.delete(i)) count++;
        }
        end = System.currentTimeMillis();
        System.out.println("delete time = " + (end - start));
        hash.print();
        System.out.println("deleted : " + count);

        start = System.currentTimeMillis();
        for (int i = 0; i < 1000_000; i++){
            hash.insert(i);
        }
        end = System.currentTimeMillis();
        System.out.println("insert time = " + (end - start));
        hash.print();
    }
}