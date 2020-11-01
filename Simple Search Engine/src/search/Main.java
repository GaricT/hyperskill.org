package search;

import search.engine.SearchEngine;
import search.vault.TextFileVault;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String fileName = getFileName(args);

        try {
            TextFileVault simpleVault = new TextFileVault(fileName);
            SearchEngine searchEngine = new SearchEngine(simpleVault);
            try(SimpleSearchEngine simpleSearchEngine = new SimpleSearchEngine(simpleVault, searchEngine)){
                simpleSearchEngine.run();
            }
        } catch (IOException e) {
            System.out.printf("File not found: '%s'\n", fileName);
        }
    }

    private static String getFileName(String[] args) {
        for (int i = 0; i < args.length - 1; i += 2) {
            if (args[i].matches("((--data)|(--db))")) {
                System.out.println(args[i + 1]);
                return args[i + 1];
            }
        }
        return "";
    }
}
