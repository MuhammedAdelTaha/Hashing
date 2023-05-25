package main.java;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class App {
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String RESET = "\033[0m";

    private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to out Dictionary");

        double type;
        int size;
        while(true){
            System.out.println("Enter 1 for NSquare-Space Hash\n" + "Enter 2 for N-Space Hash");
            String typeStr = scanner.nextLine();
            if (isNumeric(typeStr)) {
                type = Double.parseDouble(typeStr);
                if(type == 1 || type == 2)break;
            }
        }
        while(true){
            System.out.println("Please, Enter the Dictionary size");
            String sizeStr = scanner.nextLine();
            if (isNumeric(sizeStr)) {
                size = Integer.parseInt(sizeStr);
                break;
            }
        }
        Dictionary dictionary = new Dictionary(type == 1 ? "NSquare" : "N", size);

        while (true) {
            double op;
            while (true){
                System.out.println("Operations Available...");
                System.out.println("""
                        Insertion -> 1
                        Deletion -> 2
                        Search -> 3
                        Batch Insert -> 4
                        Batch Delete -> 5
                        Size -> 6
                        Exit -> 7""");
                String opStr = scanner.nextLine();
                if (isNumeric(opStr)) {
                    op = Double.parseDouble(opStr);
                    if(op >= 1 && op <= 7)break;
                }
            }

            switch ((int) op) {
                case 1:
                    System.out.println("Enter a Word");
                    String str = scanner.nextLine();
                    String[] strings = str.split(" ");
                    while (check(strings)) {
                        str = scanner.nextLine();
                        strings = str.split(" ");
                    }
                    dictionary.insert(strings[0]);
                    break;
                case 2:
                    System.out.println("What is the word you want to delete ?");
                    str = scanner.nextLine();
                    strings = str.split(" ");
                    while (check(strings)) {
                        str = scanner.nextLine();
                        strings = str.split(" ");
                    }
                    dictionary.delete(strings[0]);
                    break;
                case 3:
                    System.out.println("What is the word you want to search for ?");
                    str = scanner.nextLine();
                    strings = str.split(" ");
                    while (check(strings)) {
                        str = scanner.nextLine();
                        strings = str.split(" ");
                    }
                    dictionary.search(strings[0]);
                    break;
                case 4:
                    System.out.println("Enter file path");
                    str = scanner.nextLine();
                    int temp = dictionary.hash.size();
                    ArrayList<String> arrayList = dictionary.batchInsert(str);
                    if (arrayList.size() == 0) {
                        if(temp != dictionary.hash.size())
                            System.out.println(GREEN + "All words inserted successfully" + RESET);
                    } else {
                        System.out.println(RED + arrayList.size() + " Words failed to be inserted" + RESET);
                        log(arrayList);
                    }
                    break;
                case 5:
                    System.out.println("Enter file path");
                    str = scanner.nextLine();
                    temp = dictionary.hash.size();
                    arrayList = dictionary.batchDelete(str);
                    if (arrayList.size() == 0) {
                        if(temp != dictionary.hash.size())
                            System.out.println(GREEN + "All words deleted successfully" + RESET);
                    } else {
                        System.out.println(RED + arrayList.size() + " Words failed to be deleted" + RESET);
                        log(arrayList);
                    }
                    break;
                case 6:
                    dictionary.size();
                    break;
                case 7:
                    return;
            }
            System.out.print("\n");
        }
    }

    private static void log(ArrayList<String> arrayList) {
        try {
            FileWriter fileWriter = new FileWriter(
                    "files/log.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);

            for (String item : arrayList) {
                printWriter.println(item);
            }
            printWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + e.getMessage());
        }
    }

    private static boolean check(String[] strings) {
        if (strings.length != 1) {
            System.out.println(RED + "One word only!" + RESET);
            return true;
        }
        return false;
    }
}