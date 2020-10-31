package search.vault;

import java.util.List;
import java.util.Set;

public interface Vault {
    List<String> getAll();

    String getRecord(Integer id);

    List<String> getRecords(Set<Integer> ids);

    List<Integer> getWordOccursId(String word);

    Set<Integer> getIndexes();
}
