import java.util.ArrayList;
import java.util.Random;

public class NSpace {

    long count = 0;
    long M;
    long size;
    long sizeSum = 0;
    int[][] table;
    byte[][] mainHash;
    byte[][][] secondaryHashes;
    int m[];
    int b[];
    int s[];
    private ArrayList<Integer> deletedKeys = new ArrayList<>();
    private ArrayList<byte[][]> universalHashes = new ArrayList<>();
    private final int BLANK = Integer.MIN_VALUE;
    private final int u = 32;

    NSpace() {
        rebuildTable(BLANK);
    }

    boolean insert(int value) {
        if (search(value))
            return false;

        count++;
        if (count > M) {
            rebuildTable(value);
            return true;
        }
        
        int i = hash(mainHash, value, size);
        int j = hash(secondaryHashes[i], value, s[i]);
        if (table[i].length != 0 && table[i][j] == value) {
            if (deletedKeys.contains(table[i][j]))
                deletedKeys.remove(table[i][j]);
            return true;
        }

        b[i]++;
        
        if (b[i] <= m[i]) {
            if (table[i][j] == BLANK) {
                table[i][j] = value;
                return true;
            }
            
            ArrayList<Integer> list = new ArrayList<>();
            for (int index = 0; index < s[i]; index++) {
                if (table[i][index] != BLANK && !deletedKeys.contains(table[i][index]))
                    list.add(table[i][index]);
            }
            list.add(value);
            
            b[i] = list.size();
    
            table[i] = new int[s[i]];
            for (int k = 0; k < s[i]; k++)
                table[i][k] = BLANK;

            secondaryHashes[i] = getInjectiveFunction(list, s[i]);

            for (var element : list)
                table[i][hash(secondaryHashes[i], element, s[i])] = element;

            return true;
        }

        int oldSize = s[i];
        m[i] = 2 * Math.max(1, m[i]);
        s[i] = 2 * m[i] * (m[i] - 1);

        sizeSum -= oldSize;
        sizeSum += s[i];

        long limit = 32 * M * M / size + 4 * M;

        if (sizeSum <= limit) {
            ArrayList<Integer> list = new ArrayList<>();
            if (table[i].length != 0)
                for (int index = 0; index < table[i].length; index++)
                    try {
                        if (table[i][index] != BLANK && !deletedKeys.contains(table[i][index]))
                            list.add(table[i][index]);
                    } catch (Exception e) {
                        System.out.println("DSD");
                    }

            list.add(value);

            b[i] = list.size();
    
            table[i] = new int[s[i]];
            for (int k = 0; k < s[i]; k++)
                table[i][k] = BLANK;
            
            secondaryHashes[i] = getInjectiveFunction(list, s[i]);

            for (var element : list)
                table[i][hash(secondaryHashes[i], element, s[i])] = element;
            
            return true;
        }

        rebuildTable(value);
        return true;
    }

    

    boolean delete(int value) {
        if (!search(value))
            return false;
    
        count++;
    
        deletedKeys.add(value);
    
        if (count >= M)
            rebuildTable(BLANK);
    
        return true;
    }
    
    boolean search(int value) {
        if (deletedKeys.contains(value))
            return false;
    
        int i = hash(mainHash, value, size);
        int j = hash(secondaryHashes[i], value, s[i]);
    
        if (table[i].length != 0 && table[i][j] == value)
            return true;
        else
            return false;
    }

    private void rebuildTable(int value) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < table[i].length; j++)
                if (table[i][j] != BLANK && !deletedKeys.contains(table[i][j]))
                    list.add(table[i][j]);

        if (value != BLANK)
            list.add(value);

        count = list.size();

        M = 2 * Math.max(count, 4);
        size = (int)(1.5 * Math.max(M, 8));

        secondaryHashes = new byte[(int)size][][];
        table = new int[(int)size][];

        int bits = (int)Math.ceil(Math.log(size) / Math.log(2));
        
        if (size == 1)
            bits = 1;
            
        int sum = 0;
        ArrayList<Integer> newTable[] = new ArrayList[(int)size];
        long limit = 32 * M * M / size + 4 * M;
        do {
            for (int i = 0; i < size; i++)
                newTable[i] = new ArrayList<>();

            sum = 0;

            s = new int[(int)size];
            b = new int[(int)size];
            m = new int[(int)size];

            byte[][] hash = new byte[bits][u];
            Random r = new Random();
            
            for (int i = 0; i < bits; i++) {
                for (int j = 0; j < u; j++) {
                    hash[i][j] = (byte)r.nextInt(2);
                }
            }

            for (var element : list)
                try {
                    newTable[hash(hash, element, size)].add(element);
                }
                catch (Exception e) {
                    System.out.println("sadsa");
                }

            for (int i = 0; i < size; i++) {
                if (newTable[i].size() != 0)
                    b[i] = newTable[i].size();
                else
                    b[i] = 0;

                m[i] = 2 * b[i];
                s[i] = 2 * m[i] * (m[i] - 1);
                sum += s[i];
            }

            mainHash = hash;

        } while (sum > limit);

        sizeSum = sum;

        for (int i = 0; i < size; i++) {
            if (newTable[i].size() != 0)
                secondaryHashes[i] = getInjectiveFunction(newTable[i], s[i]);

            table[i] = new int[s[i]];
            for (int k = 0; k < s[i]; k++)
                table[i][k] = BLANK;

            if (newTable[i].size() != 0)
                for (var x : newTable[i])
                    table[i][hash(secondaryHashes[i], x, s[i])] = x;
        }
    }

    private int hash(byte[][] h, int key, long size) {
        if (h == null)
            return 0;

        String binaryString = Integer.toBinaryString(key);
        // number of bits in the given key
        // int u = lg(key) + 1;
        // m = 2 ^ b , b is the number of rows in h
        int b = lg((int)size);
        byte[] x = new byte[u];

        // fill the actual bit representation of the given key
        int keyCopy = key;
        for (int i = 0; i < u; i++) {
            // `try {
            //     x[i] = (byte) (binaryString.charAt(u - i - 1) - '0');
            // } catch (Exception e) {
            //     System.out.println("dsfds");
            // }

            x[i] = (byte)(keyCopy % 2);
            keyCopy /= 2;
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
        return hx % (int)size;
    }

    private byte[][] makeHash(ArrayList<Integer> list, int size) {
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

    private byte[][] getInjectiveFunction(ArrayList<Integer> list, int size) {
        for (var function : universalHashes) {
            if (function.length < lg(size) + 1)
                continue;

            boolean collision = false;
            ArrayList<Integer> indices = new ArrayList<>();
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

        return makeHash(list, size);
    }

    private int lg(int num) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(num) - 1;
    }
}


// function FullRehash(x) is
//     Put all unmarked elements of T in list L;
//     if (x is in U) 
//         append x to L;
//     end if
//     count = length of list L;
//     M = (1 + c) * max{count, 4};
//     repeat 
//         h = randomly chosen function in Hs(M);
//         for all j < s(M) 
//             form a list Lj for h(x) = j;
//             bj = length of Lj; 
//             mj = 2 * bj; 
//             sj = 2 * mj * (mj - 1);
//         end for
//     until the sum total of all sj ≤ 32 * M2 / s(M) + 4 * M
//     for all j < s(M) 
//         Allocate space sj for subtable Tj;
//         repeat 
//             hj = randomly chosen function in Hsj;
//         until hj is injective on the elements of list Lj;
//     end for
//     for all x on list Lj 
//         store x in position hj(x) of Tj;
//     end for
// end


// function Delete(x) is
//     count = count + 1;
//     j = h(x);
//     if position hj(x) of subtable Tj contains x
//         mark x as deleted;
//     end if
//     else 
//         return (x is not a member of S);
//     end else
//     if (count >= M)
//         FullRehash(-1);
//     end if
// end



// function Insert(x) is
//     count = count + 1;
//     if (count > M) 
//         FullRehash(x);
//     end if
//     else
//         j = h(x);
//         if (Position hj(x) of subtable Tj contains x)
//             if (x is marked deleted) 
//                 remove the delete marker;
//             end if
//         end if
//         else
//             bj = bj + 1;
//             if (bj <= mj) 
//                 if position hj(x) of Tj is empty 
//                     store x in position hj(x) of Tj;
//                 end if
//                 else
//                     Put all unmarked elements of Tj in list Lj;
//                     Append x to list Lj;
//                     bj = length of Lj;
//                     repeat 
//                         hj = randomly chosen function in Hsj;
//                     until hj is injective on the elements of Lj;
//                     for all y on list Lj
//                         store y in position hj(y) of Tj;
//                     end for
//                 end else
//             end if
//             else
//                 mj = 2 * max{1, mj};
//                 sj = 2 * mj * (mj - 1);
//                 if the sum total of all sj ≤ 32 * M2 / s(M) + 4 * M 
//                     Allocate sj cells for Tj;
//                     Put all unmarked elements of Tj in list Lj;
//                     Append x to list Lj;
//                     bj = length of Lj;
//                     repeat 
//                         hj = randomly chosen function in Hsj;
//                     until hj is injective on the elements of Lj;
//                     for all y on list Lj
//                         store y in position hj(y) of Tj;
//                     end for
//                 end if
//                 else
//                     FullRehash(x);
//                 end else
//             end else
//         end else
//     end else
// end