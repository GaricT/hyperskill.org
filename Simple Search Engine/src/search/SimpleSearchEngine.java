package search;

import search.engine.SearchEngine;
import search.vault.Vault;

import java.io.Closeable;
import java.util.List;
import java.util.Scanner;

public class SimpleSearchEngine implements Closeable {
    private final Scanner scanner;
    private final Vault vault;
    private final SearchEngine searchEngine;

    public SimpleSearchEngine(Vault vault, SearchEngine searchEngine) {
        this.vault = vault;
        this.searchEngine = searchEngine;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            prnMenu();

            if (!userInput(Integer.valueOf(nextInput("")))) {
                break;
            }
            System.out.println("");
        }
    }

    private boolean userInput(Integer userInput) {
        switch (userInput) {
            case 0:
                return menuClose();

            case 1:
                return menuFind();

            case 2:
                return menuPrintAll();

            default:
                return menuWrongChoice();
        }
    }

    public void prnMenu() {
        System.out.println("=== Menu ===");
        System.out.println("1. Find a person");
        System.out.println("2. Print all people");
        System.out.println("0. Exit");
    }

    private void prnResultSearch(List<String> users) {
        if (users.isEmpty()) {
            System.out.println("No matching people found.");
        } else {
            System.out.println(users.size() + " persons found:");
            users.forEach(System.out::println);
        }
    }

    private String nextInput(String message) {
        if (!message.isEmpty()) {
            System.out.println(message);
        }
        String result = scanner.nextLine();
        System.out.println("");
        return result;
    }

    private boolean menuClose() {
        System.out.println("Bye!");
        return false;
    }

    private boolean menuFind() {
        String choice = nextInput("Select a matching strategy: ALL, ANY, NONE").toUpperCase();
        String words = nextInput("Enter a name or email to search all suitable people.");

        List<String> search = searchEngine.search(words.split(" "), choice);

        prnResultSearch(search);
        return true;
    }

    private boolean menuPrintAll() {
        System.out.println("=== List of people ===");
        vault.getAll().forEach(System.out::println);
        return true;
    }

    private boolean menuWrongChoice() {
        System.out.println("Incorrect option! Try again.");
        return true;
    }

    @Override
    public void close() {
        scanner.close();
    }
}
