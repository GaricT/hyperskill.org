package search.vault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TextFileVault implements Vault {

    private List<String> data = new LinkedList<>();
    private Map<String, List<Integer>> inverseMap = new HashMap<>();

    public TextFileVault(String fileName) throws IOException {
        Function<String, List<Integer>> getList = s -> new LinkedList<>();
        try (Stream<String> lines = Files.lines(Path.of(fileName))) {
            lines.forEach(s -> {
                data.add(s);
                Arrays.asList(s.split(" ")).forEach(
                        v -> inverseMap.computeIfAbsent(v, getList).add(data.size() - 1));
            });
            }
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
