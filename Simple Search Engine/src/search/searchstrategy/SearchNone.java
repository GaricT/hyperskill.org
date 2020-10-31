package search.searchstrategy;

import search.vault.Vault;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchNone implements SearchStrategy {
    private Vault vault;

    public SearchNone(Vault vault) {
        this.vault = vault;
    }

    @Override
    public List<String> search(Set<String> words) {
        Set<Integer> inverseIndexes = vault.getIndexes();
        words.stream()
                .map(s -> vault.getWordOccursId(s))
                .forEach(inverseIndexes::removeAll);
        return vault.getRecords(inverseIndexes);
    }
}
