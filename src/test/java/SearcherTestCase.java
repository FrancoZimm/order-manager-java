import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import com.example.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.model.Searcher;

public class SearcherTestCase {
    private Searcher searcher;

    @BeforeEach
    void setUp() {
        searcher = new Searcher();
    }

    @Test
    @DisplayName("Test: Word exists in list")
    void testSearchWordExists() {
        List<String> list = List.of("apple", "banana", "cherry");
        boolean result = searcher.searchWord("banana", list);
        assertEquals(true, result);
    }

    @Test
    @DisplayName("Test: Word does not exist in list")
    void testSearchWordNotExists() {
        List<String> list = List.of("apple", "banana", "cherry");
        boolean result = searcher.searchWord("date", list);
        assertEquals(false, result);
    }

    @Test
    @DisplayName("Test: Get word by valid index")
    void testGetWordByValidIndex() {
        List<String> list = List.of("apple", "banana", "cherry");
        String result = searcher.getWordByIndex(list, 1);
        assertEquals("banana", result);
    }

    @Test
    @DisplayName("Test: Get word by invalid index")
    void testGetWordByInvalidIndex() {
        List<String> list = List.of("apple", "banana", "cherry");
        String result = searcher.getWordByIndex(list, 5);
        assertEquals(null, result);
    }

    @Test
    @DisplayName("Test: devuelve las palabras que empiezan con el prefijo")
    void testSearchByPrefixMatches() {
        List<String> list = List.of("presto", "prefix", "other", "prefijo", "pre");
        List<String> result = searcher.searchByPrefix("pre", list);

        assertTrue(result.containsAll(List.of("presto", "prefix", "prefijo", "pre")));
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("Test: no incluye palabras que no empiecen con el prefijo")
    void testSearchByPrefixNoMatches() {
        List<String> list = List.of("apple", "banana", "presto", "prefix");
        List<String> result = searcher.searchByPrefix("pre", list);

        assertFalse(result.contains("apple"));
        assertFalse(result.contains("banana"));
    }

    @Test
    @DisplayName("Test: devuelve todos los elementos que contienen la keyword")
    void testFilterByKeywordMatches() {
        List<String> list = List.of("hola mundo", "adios mundo", "hola chat");
        List<String> result = searcher.filterByKeyword("mundo", list);

        assertTrue(result.containsAll(List.of("hola mundo", "adios mundo")));
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test: devuelve vacío si la keyword no existe")
    void testFilterByKeywordEmpty() {
        List<String> list = List.of("hola", "adios");
        List<String> result = searcher.filterByKeyword("xyz", list);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Test: encuentra frase exacta aunque no sea el primer elemento")
    void testSearchExactPhraseExists() {
        List<String> list = List.of("uno", "dos exacto", "tres");
        boolean result = searcher.searchExactPhrase("dos exacto", list);
        assertTrue(result, "Debe encontrar la frase aunque no sea el primer elemento");
    }

    @Test
    @DisplayName("Test: devuelve false si la frase no existe")
    void testSearchExactPhraseNotExists() {
        List<String> list = List.of("uno", "dos exacto", "tres");
        boolean result = searcher.searchExactPhrase("cuatro", list);
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Test: searchWord with empty list returns false")
    void testSearchWordEmptyList() {
        List<String> list = List.of();
        boolean result = searcher.searchWord("banana", list);
        assertFalse(result);
    }

    @Test
    @DisplayName("Test: getWordByIndex with negative index returns null")
    void testGetWordByNegativeIndex() {
        List<String> list = List.of("apple", "banana");
        String result = searcher.getWordByIndex(list, -1);
        assertEquals(null, result);
    }

    @Test
    @DisplayName("Test: searchByPrefix with empty prefix returns all")
    void testSearchByPrefixEmptyPrefix() {
        List<String> list = List.of("apple", "banana");
        List<String> result = searcher.searchByPrefix("", list);
        assertEquals(list, result);
    }

    @Test
    @DisplayName("findById: returns order when id exists")
    void testFindByIdFound() {
        Order o1 = new Order("A1", List.of());
        Order o2 = new Order("B2", List.of());
        List<Order> orders = List.of(o1, o2);

        Searcher searcher = new Searcher();
        Order found = searcher.findById(orders, "B2");

        assertNotNull(found);
        assertEquals("B2", found.getId());
    }

    @Test
    @DisplayName("findById: returns null when id not present")
    void testFindByIdNotFound() {
        Order o1 = new Order("A1", List.of());
        List<Order> orders = List.of(o1);

        Searcher searcher = new Searcher();
        assertNull(searcher.findById(orders, "X99"));
    }

    @Test
    @DisplayName("findById: null or blank id returns null")
    void testFindByIdBlankOrNullId() {
        Order o1 = new Order("A1", List.of());
        List<Order> orders = List.of(o1);

        Searcher searcher = new Searcher();
        assertNull(searcher.findById(orders, ""));
        assertNull(searcher.findById(orders, "   "));
        assertNull(searcher.findById(orders, null));
    }

    @Test
    @DisplayName("findById: null orders list throws NullPointerException")
    void testFindByIdNullOrdersThrows() {
        // use the test fixture field 'searcher' initialized in @BeforeEach
        NullPointerException ex = assertThrows(NullPointerException.class, () -> this.searcher.findById(null, "A1"));
        assertEquals("orders must not be null", ex.getMessage());
    }
}
