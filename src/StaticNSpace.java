import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class StaticNSpace {
    int n;
    int count = 0;
    int[][] table;
    byte[][] mainHash;
    byte[][][] secondaryHashes;
    private ArrayList<byte[][]> universalHashes = new ArrayList<>();
    private final int BLANK = Integer.MIN_VALUE;
    private final int u = 32;

    public StaticNSpace(int n) {
        this.n = n;
        table = new int[n][1];
        Arrays.fill(table, new int[1]);
        for (int i = 0; i < n; i++)
            Arrays.fill(table[i], BLANK);
        secondaryHashes = new byte[n][][];
    }

    public int size() {
        return count;
    }

    public int batchInsert(int[] values) {
        final int limit = 2 * n;
        int sum = 0;
        int successfullyInserted;
        ArrayList<Integer>[] newTable;
        do {
            sum = 0;
            successfullyInserted = 0;

            mainHash = makeHash(n);

            newTable = new ArrayList[n];
            for (int i = 0; i < newTable.length; i++)
                newTable[i] = new ArrayList<Integer>();

            for (int i = 0; i < values.length; i++) {
                successfullyInserted++;
                int index = hash(mainHash, values[i], n);
                newTable[index].add(values[i]);
            }

            for (int i = 0; i < n; i++)
                sum += newTable[i].size() * newTable[i].size();
        } while (sum > limit);

        for (int i = 0; i < n; i++) {
            table[i] = new int[newTable[i].size() * newTable[i].size()];

            Arrays.fill(table[i], BLANK);

            if (newTable[i].size() > 1) {
                secondaryHashes[i] = getInjectiveFunction(newTable[i], newTable[i].size() * newTable[i].size());
                for (int x : newTable[i]) {
                    int subIndex = hash(secondaryHashes[i], x, newTable[i].size() * newTable[i].size());
                    table[i][subIndex] = x;
                }
            }
            else if (newTable[i].size() == 1)
                table[i][0] = newTable[i].get(0);
        }

        count += successfullyInserted;

        return successfullyInserted;
    }

    public boolean insert(int value) {
        if (search(value))
            return false;

        count++;

        int i = hash(mainHash, value, n);
        int j = 0;

        if (table[i].length == 1)
            j = 0;
        else if (table[i].length == 0)
            table[i] = new int[1];
        else
            j = hash(secondaryHashes[i], value, table[i].length);
        
        if (table[i][j] != BLANK) {
            ArrayList<Integer> newSubTable = new ArrayList<>();
            for (var x : table[i])
                if (x != BLANK)
                    newSubTable.add(x);
            
            newSubTable.add(value);

            secondaryHashes[i] = getInjectiveFunction(newSubTable, newSubTable.size() * newSubTable.size());

            table[i] = new int[newSubTable.size() * newSubTable.size()];
            Arrays.fill(table[i], BLANK);

            for (var x : newSubTable) {
                int subIndex = hash(secondaryHashes[i], x, newSubTable.size() * newSubTable.size());
                table[i][subIndex] = x;
            }
        }
        else
            table[i][j] = value;

        return true;
    }

    public boolean delete(int value) {
        if (!search(value))
            return false;
    
        count--;

        int i = hash(mainHash, value, n);
        int j;

        if (table[i].length == 1)
            j = 0;
        else
            j = hash(secondaryHashes[i], value, table[i].length);
    
        table[i][j] = BLANK;
    
        return true;
    }
    
    public boolean search(int value) {
        int i = hash(mainHash, value, n);
        int j = 0;

        if (table[i].length == 0)
            return false;
        if (table[i].length == 1)
            j = 0;
        else
            j = hash(secondaryHashes[i], value, table[i].length);

    
        if (table[i][j] == value)
            return true;
        else
            return false;
    }

    private int hash(byte[][] h, int key, long size) {
        if (h == null)
            return 0;

        int b = (int)Math.ceil(Math.log(size) / Math.log(2));
        byte[] x = new byte[u];

        int keyCopy = key;
        for (int i = 0; i < u; i++) {
            x[i] = (byte)(keyCopy % 2);
            keyCopy /= 2;
        }

        // calculate the hash index and return it
        int hx = 0;
        for (int i = 0; i < b; i++) {
            int bit = 0;
            for (int j = 0; j < u; j++) {
                bit += h[i][j] * x[j];
            }
            bit %= 2;
            hx += bit * (1 << i);
        }
        return hx % (int)size;
    }

    private byte[][] getInjectiveFunction(ArrayList<Integer> list, int size) {
        for (var function : universalHashes) {
            if (function.length < Math.ceil(Math.log(size) / Math.log(2)))
                continue;

            boolean collision = false;
            ArrayList<Integer> indices = new ArrayList<>(size);
            for (var element : list) {
                int index = hash(function, element, size);

                if (indices.contains(index)) {
                    collision = true;
                    break;
                }

                indices.add(index);
            }

            if (!collision)
                return function;
        }

        return makeInjectiveHash(list, size);
    }

    private byte[][] makeInjectiveHash(ArrayList<Integer> list, int size) {
        int b = (int)Math.ceil(Math.log(size) / Math.log(2));

        if (size == 1)
            b = 1;

        boolean collision = true;
        while (collision) {
            collision = false;

            byte[][] hash = new byte[b][u];
            Random r = new Random();
            
            for (int i = 0; i < b; i++) {
                for (int j = 0; j < u; j++) {
                    hash[i][j] = (byte)r.nextInt(2);
                }
            }
            
            ArrayList<Integer> indices = new ArrayList<>();
            for (var element : list) {
                int index = hash(hash, element, size);

                if (indices.contains(index)) {
                    collision = true;
                    break;
                }

                indices.add(index);
            }

            if (!collision) {
                universalHashes.add(hash);
                return hash;
            }
        }

        System.err.println("UNREACHABLE CODE!!");
        return null;
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
}
