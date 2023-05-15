import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Random;

public class NSquareSpaceHash {
    //The number of inserted keys
    private int n = 0;
    //The size of the hash table
    private int m = 2;
    //The inserted keys
    private ArrayList<Integer> insertedKeys = new ArrayList<>();
    //The deleted keys
    private ArrayList<Integer> deletedKeys = new ArrayList<>();
    //The hash table
    private final ArrayList<Integer> hashTable = new ArrayList<>();
    //Array list of hash functions used in insertions (storing them for search purpose)
    private final ArrayList<byte[][]> hashFunctions = new ArrayList<>();
    public NSquareSpaceHash(){
        resetHashTable();
    }

    /**
     * For debugging
     * */
    public void print(){
        System.out.println("n = " + n);
        System.out.println("m = " + m);
        System.out.println("number of hash functions = " + hashFunctions.size());
        System.out.println("hash table size = " + hashTable.size());
//        System.out.println("hash table : " + hashTable);
//        System.out.println("hash functions : ");
//        for (byte[][] hashFunction : hashFunctions){
//            int s2 = hashFunction[0].length;
//            for (byte[] bytes : hashFunction) {
//                System.out.print('[');
//                for (int j = 0; j < s2; j++) {
//                    System.out.print(bytes[j]);
//                    if (j != s2 - 1) System.out.print(' ');
//                }
//                System.out.println(']');
//            }
//            System.out.println("----------------------------------------");
//        }
//        System.out.println("----------------------------------------");
//        System.out.println("inserted keys : " + insertedKeys);
    }
    /**
     * Log to the base 2
     * */
    private int lg(int num){
        return Integer.SIZE - Integer.numberOfLeadingZeros(num) - 1;
    }
    /**
     * Set the initial values of the hash table to be null
     * */
    private void resetHashTable(){
        hashTable.clear();
        for (int i = 0; i < m; i++){
            hashTable.add(i, null);
        }
    }
    /**
     * Takes a randomly selected hash 2D 0/1 matrix h and the key to be inserted
     * Return an integer represents the index of the input key in the hash table using this h matrix
     * */
    private int hash(byte[][] h, int key){
        String binaryString = Integer.toBinaryString(key);
        // number of bits in the given key
        int u = lg(key) + 1;
        // m = 2 ^ b , b is the number of rows in h
        int b = lg(m);
        byte[] x = new byte[u];

        // fill the actual bit representation of the given key
        for (int i = 0; i < u; i++){
            x[i] = (byte) (binaryString.charAt(u - i - 1) - '0');
        }

        // calculate the hash index and return it
        int hx = 0;
        for (int i = 0; i < b; i++){
            int bit = 0;
            for (int j = 0; j < u; j++){
                bit += h[i][j] * x[j];
            }
            bit %= 2;
            hx += bit * (1 << i);
        }
        return hx;
    }
    /**
     * Takes a key and gets its appropriate hash function and index that gets a hash index that does not cause collision
     * */
    private AbstractMap.SimpleEntry<byte[][], Integer> hashFunction(int key){
        int u = lg(key) + 1;
        int b = lg(m);
        byte[][] h = new byte[b][u];
        Random random = new Random();
        int hashIdx;
        do {
            for (int i = 0; i < b; i++) {
                for (int j = 0; j < u; j++) {
                    h[i][j] = (byte) random.nextInt(2);
                }
            }
            hashIdx = hash(h, key);
        } while (hashTable.get(hashIdx) != null);
        return new AbstractMap.SimpleEntry<>(h, hashIdx);
    }
    /**
     * Takes a key to be inserted and update tha hash table and tha array list of used hash functions accordingly
     * */
    private void updateTables(int key){
        int hashIdx;
        for (byte[][] hashFunction : hashFunctions){
            if(hashFunction[0].length != lg(key) + 1) continue;
            hashIdx = hash(hashFunction, key);
            if (hashTable.get(hashIdx) == null){
                hashTable.set(hashIdx, key);
                return;
            }
        }
        AbstractMap.SimpleEntry<byte[][], Integer> function = hashFunction(key);
        hashFunctions.add(function.getKey());
        hashTable.set(function.getValue(), key);
    }
    /**
     * Rehashes the hash table according to the new m (hash table size)
     * */
    private void rehash(){
        hashFunctions.clear();
        resetHashTable();
        for (int key : insertedKeys){
            if (deletedKeys.contains(key)) continue;
            updateTables(key);
        }
    }
    /**
     * We do this growing when the number of inserted keys is greater than the size of the hash table
     * */
    private void grow(){
        int MAXVALUE = 100_000_000;
        if(m > Math.sqrt(Integer.MAX_VALUE))
            m = MAXVALUE;
        else
            m *= m;
        rehash();
    }
    /**
     * Takes an array list of elements and a key to be deleted from this list and returns the list after deletion
     * */
    private ArrayList<Integer> remove(ArrayList<Integer> elements, int key){
        ArrayList<Integer> copy = new ArrayList<>();
        for (Integer element : elements) {
            if (element == key) continue;
            copy.add(element);
        }
        return (ArrayList<Integer>) copy.clone();
    }
    /**
     * takes a key and inserts it in the hash table
     * */
    public boolean insert(int key){
        if(this.search(key).getKey()) return false;
        n++;
        if (deletedKeys.contains(key)) {
            deletedKeys = remove(deletedKeys, key);
            insertedKeys = remove(insertedKeys, key);
        }
        insertedKeys.add(key);
        if(n > m){
            grow();
        }
        else{
            updateTables(key);
        }
        return true;
    }
    /**
     * takes a key and deletes it from the hash table
     * */
    public boolean delete(int key){
        AbstractMap.SimpleEntry<Boolean, Integer> searcher = search(key);
        boolean searchFlag = searcher.getKey();
        if(!searchFlag)
            return false;

        int searchIdx = searcher.getValue();
        n--;
        deletedKeys.add(key);
        hashTable.set(searchIdx, null);
        return true;
    }
    /**
     * Takes a key and return true if found and false if not
     * */
    public AbstractMap.SimpleEntry<Boolean, Integer> search(int key){
        for (byte[][] hashFunction : hashFunctions){
            if(hashFunction[0].length != lg(key) + 1) continue;
            int hashIdx = hash(hashFunction, key);
            if(hashTable.get(hashIdx) != null && hashTable.get(hashIdx) == key) {
                return new AbstractMap.SimpleEntry<>(true, hashIdx);
            }
        }
        return new AbstractMap.SimpleEntry<>(false, null);
    }
}