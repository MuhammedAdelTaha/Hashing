package main.java;

import main.java.Hashing.Hash;
import main.java.Hashing.NSquareSpaceHash;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Dictionary {
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public Hash hash;
    private final ArrayList<String> words = new ArrayList<>();

    public Dictionary(String type){
        if(Objects.equals(type, "NSquare")){
            System.out.println(GREEN + "NSquare-Space based Dictionary..." + RESET);
            hash = new NSquareSpaceHash();
        }else if(Objects.equals(type , "N")){
            System.out.println(GREEN + "N-Space based Dictionary..." + RESET);
//            hashTable = new RedBlackTree< String >();
        }
    }

    public void insert(String str){
        boolean ok = hash.insert(hash.preHash(str));
        if(ok){
            System.out.println(GREEN + str + " Inserted Successfully" + RESET);
        }else{
            System.out.println(RED + "Insertion Failed" + RESET);
        }
    }

    public void delete(String str){
        boolean ok = hash.delete(hash.preHash(str));
        if(ok){
            System.out.println(GREEN + str + " Deleted Successfully" + RESET);
        }else{
            System.out.println(RED + "This word doesn't exist" + RESET);
        }
    }

    public void search(String str){
        boolean ok = hash.search(hash.preHash(str)).getKey();
        if(ok){
            System.out.println(GREEN + str + " Found" + RESET);
        }else{
            System.out.println(RED + str + " Not Found" + RESET);
        }
    }

    public ArrayList < String > batchInsert(String path){
        words.clear();
        parse(path);
        ArrayList < String > notInserted = new ArrayList<>();
        for (String s : words){
            if(hash.insert(hash.preHash(s))) continue;
            notInserted.add(s);
        }
        return notInserted;
    }

    public ArrayList < String > batchDelete(String path){
        words.clear();
        parse(path);
        ArrayList < String > notDeleted = new ArrayList<>();
        for (String s : words){
            if(hash.delete(hash.preHash(s))) continue;
            notDeleted.add(s);
        }
        return notDeleted;
    }

    public void size(){
        int x = hash.size();
        if(x == 0){
            System.out.println(RED + "The Dictionary is Empty" + RESET);
        }else{
            System.out.println(GREEN + "The Dictionary size = " + x + RESET);
        }
    }

    private void parse(String filePath){
        try (BufferedReader buffer = new BufferedReader(new FileReader(filePath))) {
            String str;
            while ((str = buffer.readLine()) != null) {
                this.words.add(str);
            }
        }
        catch (IOException e) {
            System.out.println(RED + "Please, enter a valid path..." + RESET);
        }
    }
}
