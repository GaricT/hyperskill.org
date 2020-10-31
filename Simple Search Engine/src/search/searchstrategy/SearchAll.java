package search.searchstrategy;

import search.vault.Vault;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchAll implements SearchStrategy {
    private Vault vault;

    public SearchAll(Vault vault) {
        this.vault = vault;
    }

    @Override
    public List<String> search(Set<String> words) {
        Set<Integer> inverseIndexes = new HashSet<>();
        if (!words.isEmpty()) {
            inverseIndexes.addAll(vault.getWordOccursId(words.iterator().next()));
        }
        words.stream()
                .map(s -> vault.getWordOccursId(s))
                .forEach(inverseIndexes::retainAll);
        return vault.getRecords(inverseIndexes);
    }
}
