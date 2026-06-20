package util;

import java.util.Scanner;

public class ConsoleGameIO implements GameIO {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void show(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine() {
        if (!scanner.hasNextLine()) {
            return "0";
        }

        return scanner.nextLine();
    }
}
