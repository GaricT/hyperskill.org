package search.engine;

import search.searchstrategy.SearchAll;
import search.searchstrategy.SearchAny;
import search.searchstrategy.SearchNone;
import search.searchstrategy.SearchStrategy;
import search.vault.Vault;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchEngine {
    private Map<String, SearchStrategy> strategyMap = new HashMap<>();

    public SearchEngine(Vault vault) {
        strategyMap.put("ALL", new SearchAll(vault));
        strategyMap.put("ANY", new SearchAny(vault));
        strategyMap.put("NONE", new SearchNone(vault));
    }

    public List<String> search(String[] words, String strategy) {
        return search(Arrays.stream(words).collect(Collectors.toSet()), strategy);
    }

    public List<String> search(List<String> words, String strategy) {
        return search(new HashSet<>(words), strategy);
    }

    public List<String> search(Set<String> words, String strategy) {
        SearchStrategy defaultStrategy = s -> Collections.emptyList();
        return strategyMap.getOrDefault(strategy, defaultStrategy).search(words);
    }
}
