package search.vault;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextFileVault implements Vault {

    private List<String> data = new LinkedList<>();
    private Map<String, List<Integer>> inverseMap = new HashMap<>();

    public TextFileVault(String fileName) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNext()) {
                String value = scanner.nextLine();
                data.add(value);
                Arrays.stream(value.split(" "))
                        .forEach(s -> inverseMap
                                .computeIfAbsent(s.toLowerCase(), v -> new LinkedList()).add(data.size() - 1));
            }
        }
    }

    public List<String> getUsersByFilter(String value) {
        return inverseMap.getOrDefault(value, new LinkedList<>()).stream()
                .map(s -> data.get(s)).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getWordOccursId(String word) {
        return new ArrayList<>(inverseMap.getOrDefault(word.toLowerCase(), new LinkedList<>()));
    }

    @Override
    public List<String> getAll() {
        return data;
    }

    @Override
    public String getRecord(Integer id) {
        return data.get(id);
    }

    @Override
    public List<String> getRecords(Set<Integer> ids) {
        return ids.stream()
                .collect(
                        ArrayList::new,
                        (l, r) -> l.add(data.get(r)),
                        ArrayList::addAll);
    }

    @Override
    public Set<Integer> getIndexes() {
        return IntStream.range(0, data.size()).boxed().collect(Collectors.toSet());
    }
}
