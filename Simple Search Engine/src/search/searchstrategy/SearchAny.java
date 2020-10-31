package search.searchstrategy;

import search.searchstrategy.SearchStrategy;
import search.vault.Vault;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchAny implements SearchStrategy {
    private Vault vault;

    public SearchAny(Vault vault) {
        this.vault = vault;
    }

    @Override
    public List<String> search(Set<String> words) {
        Set<Integer> inverseIndexes = words.stream()
                .flatMap(s -> vault.getWordOccursId(s).stream())
                .collect(Collectors.toSet());
        return vault.getRecords(inverseIndexes);
    }
}
