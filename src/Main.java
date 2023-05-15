public class Main {
    public static void main(String[] args) {
        Hash hash = new Hash();
        for (int i = 0; i < 20000; i++){
            hash.insert(i);
        }
        hash.print();

        int count = 0;
        for (int i = 0; i < 200000; i++){
            if(hash.search(i).getKey()) count++;
        }
        System.out.println("true : " + count);

        System.out.println(hash.delete(200000));
        System.out.println(hash.delete(20000));
        System.out.println(hash.delete(19999));
        System.out.println(hash.delete(2000));
        System.out.println(hash.delete(200));
        System.out.println(hash.delete(0));

        System.out.println(hash.search(200000));
        System.out.println(hash.search(20000));
        System.out.println(hash.search(2000));
        System.out.println(hash.search(200));
    }
}