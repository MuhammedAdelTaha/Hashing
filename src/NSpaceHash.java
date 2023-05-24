import java.util.ArrayList;
import java.util.Random;

public class NSpaceHash {
    private int n = 0;
    private int actual = 0;
    private int m = 32;                             // intitial table size
    private int u = 20;                             // key size in bits
    private int[][] table;                          // the hash table
    private byte[][] mainHash;                      // main hash function for the main hash table
    private byte[][][] secondaryHashes;             // hash functions of each secondary hash table
    private final int BLANK = Integer.MIN_VALUE;    // dummy value for an empty slot
    ArrayList<Integer> added = new ArrayList<>();

    NSpaceHash() {
        mainHash = makeHash(m);

        table = new int[m][1];
        for (int i = 0; i < table.length; i++) {
            table[i][0] = BLANK;
        }
    }

    boolean search(int value) {
        int hash = hash(mainHash, value, table.length);
        int secondaryHash = 0;

        if (secondaryHashes != null)
            secondaryHash = hash(secondaryHashes[hash], value, table[hash].length);

        if (table[hash][secondaryHash] == value)
            return true;
        
        return false;
    }

    void insert(int value) {
        if (search(value))
            return;

        if (n + 1 > table.length) {
            rebuildTable((int)(m * 1.5));
        }

        int hash = hash(mainHash, value, m);
        
        if (table[hash][0] == BLANK) {
            table[hash][0] = value;
            n++;
        }
        else {
            actual = 0;
            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < table[i].length; j++) {
                    if (table[i][j] != BLANK) {
                        actual++;
                    }
                }
            }

            if (secondaryHashes != null && secondaryHashes[hash] != null) {
                int secondaryHash = hash(secondaryHashes[hash], value, table[hash].length);
                if (table[hash][secondaryHash] == BLANK) {
                    table[hash][secondaryHash] = value;
                    n++;
                    return;
                }
            }

            rebuildTable(m);
            insert(value);
        }
    }

    void rebuildTable(int size) {
        if (size < m)
            return;

        ArrayList<Integer>[] newTable = new ArrayList[size];

        for (int i = 0; i < newTable.length; i++) {
            newTable[i] = new ArrayList<>();
        }
        
        mainHash = makeHash(size);
        
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (table[i][j] != BLANK)
                    newTable[hash(mainHash, table[i][j], newTable.length)].add(table[i][j]);
            }
        }
            
        table = new int[size][];
        secondaryHashes = new byte[size][][];

        // for (int i = 0; i < newTable.length; i++) {
        //     table[i] = new int[newTable[i].size() * newTable[i].size()];
        //     if (newTable[i].size() == 0)
        //         table[i] = new int[1];
        //     for (int j = 0; j < table[i].length; j++) {
        //         table[i][j] = BLANK;
        //     }
        // }

        for (int i = 0; i < newTable.length; i++) {
            boolean collision = true;
            while (collision) {

                table[i] = new int[newTable[i].size() * newTable[i].size()];
                if (newTable[i].size() == 0)
                    table[i] = new int[1];
                for (int j = 0; j < table[i].length; j++) {
                    table[i][j] = BLANK;
                }

                collision = false;
                if (newTable[i].size() > 1)
                    secondaryHashes[i] = makeHash(newTable[i].size() * newTable[i].size());
                for (int j = 0; j < newTable[i].size(); j++) {
                    int val = newTable[i].get(j);
                    int hash = hash(mainHash, val, size);
                    int secondaryHash = hash(secondaryHashes[hash], val, table[hash].length);
                    
                    if (table[hash][secondaryHash] == BLANK) {
                        table[hash][secondaryHash] = val;
                    } else {
                        collision = true;
                        break;
                    }
                }
            }
        }
        m = size;
    }
    
    private byte[][] makeHash(int size) {
        int b = (int)Math.ceil(Math.log(size) / Math.log(2));

        if (size == 1)
            b = 1;
        
        byte[][] hash = new byte[b][u];
        Random r = new Random();
        
        for (int i = 0; i < b; i++) {
            for (int j = 0; j < u; j++) {
                hash[i][j] = (byte)r.nextInt(2);
            }
        }

        return hash;
    }

    private int hash(byte[][] h, int key, int size) {
        if (h == null)
            return 0;

        String binaryString = Integer.toBinaryString(key);
        // number of bits in the given key
        int u = lg(key) + 1;
        // m = 2 ^ b , b is the number of rows in h
        int b = lg(size);
        byte[] x = new byte[u];

        // fill the actual bit representation of the given key
        for (int i = 0; i < u; i++) {
            x[i] = (byte) (binaryString.charAt(u - i - 1) - '0');
        }

        // calculate the hash index and return it
        int hx = 0;
        for (int i = 0; i < b; i++) {
            int bit = 0;
            try {
                for (int j = 0; j < u; j++) {
                    bit += h[i][j] * x[j];
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            bit %= 2;
            hx += bit * (1 << i);
        }
        return hx % size;
    }

    private int lg(int num) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(num) - 1;
    }

    // void insert(int[] values) {
    //     int sum = Integer.MAX_VALUE;
    //     final int limit = 2 * values.length;

    //     long time = System.nanoTime();

    //     while (sum > limit) {
    //         table = new ArrayList[values.length];
    //         for (int i = 0; i < table.length; i++) {
    //             table[i] = new ArrayList<Integer>();
    //         }
            
    //         int b = (int)(Math.log(m) / Math.log(2));
            
    //         int[][] h = new int[b][u];
    //         for (int i = 0; i < h.length; i++) {
    //             for (int j = 0; j < h[i].length; j++) {
    //                 h[i][j] = (int)(Math.random() * 2);
    //             }
    //         }

    //         for (int k = 0; k < values.length; k++) {
    //             int value = values[k];
    //             int x[] = new int[u];
    //             for (int i = 0; i < x.length; i++) {
    //                 x[i] = value % 2;
    //                 value /= 2;
    //             }
        
    //             int hx = 0;
    //             for (int i = 0; i < h.length; i++) {
    //                 int bit = 0;
    //                 for (int j = 0; j < h[i].length; j++) {
    //                     bit += h[i][j] * x[j];
    //                 }
    //                 bit %= 2;
    //                 hx += bit * (1 << i);
    //             }
        
    //             table[hx].add(values[k]);
    //         }
    //         sum = 0;
    //         for (int i = 0; i < table.length; i++) {
    //             sum += (table[i].size() * table[i].size());
    //         }

    //         System.out.println(sum);
    //     }

    //     time = System.nanoTime() - time;

    //     System.out.println(time);


    //     int y = 2;

    // }
}