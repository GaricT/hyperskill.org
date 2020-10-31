package search.searchstrategy;

import search.vault.Vault;

import java.util.List;
import java.util.Set;

@FunctionalInterface
public interface SearchStrategy {
    List<String> search(Set<String> words);
}
