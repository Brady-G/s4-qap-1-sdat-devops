package brady.green;

import green.brady.SuggestionEngine;
import green.brady.SuggestionsDatabase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class SuggestionEngineTests {

    private SuggestionEngine engine;

    @BeforeEach
    public void setup() throws Exception {
        engine = new SuggestionEngine();
        engine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("words.txt").toURI()));
    }

    @Test
    public void testLoadDictionaryData() throws Exception {
        engine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("test_words.txt").toURI()));

        Assertions.assertAll(
                "Word suggestions db should have 2 for 'test', 'word', 'bank', 'that', 'will', 'be', 'loaded'",
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("test")),
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("word")),
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("bank")),
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("that")),
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("will")),
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("be")),
                () -> Assertions.assertEquals(2, engine.getWordSuggestionDB().get("loaded")),

                () -> Assertions.assertEquals(1, engine.getWordSuggestionDB().get("text")),
                () -> Assertions.assertEquals(1, engine.getWordSuggestionDB().get("not")),
                () -> Assertions.assertEquals(1, engine.getWordSuggestionDB().get("being")),
                () -> Assertions.assertEquals(1, engine.getWordSuggestionDB().get("tested"))
        );
    }

    @Test
    public void testCustomWordDB() throws Exception {
        SuggestionsDatabase db = new SuggestionsDatabase();
        Map<String, Integer> wordMap = new HashMap<>();
        wordMap.put("test", 2);
        wordMap.put("word", 2);
        wordMap.put("bank", 2);
        wordMap.put("that", 2);
        wordMap.put("will", 2);
        wordMap.put("be", 2);

        db.setWordMap(wordMap);
        engine.setWordSuggestionDB(db);

        engine.loadDictionaryData(Paths.get(ClassLoader.getSystemResource("test_words.txt").toURI()));

        // Check that if the custom word map is set that there should be 2 sets of words loaded at the start
        //  which means after directory loaded there will be 3 sets of words
        Assertions.assertAll(
                "Word suggestions db should have 3 for 'test', 'word', 'bank', 'that', 'will', 'be'",
                () -> Assertions.assertEquals(3, engine.getWordSuggestionDB().get("test")),
                () -> Assertions.assertEquals(3, engine.getWordSuggestionDB().get("word")),
                () -> Assertions.assertEquals(3, engine.getWordSuggestionDB().get("bank")),
                () -> Assertions.assertEquals(3, engine.getWordSuggestionDB().get("that")),
                () -> Assertions.assertEquals(3, engine.getWordSuggestionDB().get("will")),
                () -> Assertions.assertEquals(3, engine.getWordSuggestionDB().get("be")),

                // We want to make sure it still loads the file correctly, so we don't have loaded in our custom map
                //  and only get it added via load directory
                () -> Assertions.assertEquals(1, engine.getWordSuggestionDB().get("loaded"))
        );
    }

    @Test
    public void testGenerateSuggestionsImperfectWord() {
        List<String> suggestions = engine.generateSuggestions("testef").lines().toList();
        Assertions.assertAll(
                "Suggestions should contain: 'tested', 'tester', 'tests' for 'testef'",
                () -> Assertions.assertTrue(suggestions.contains("tested")),
                () -> Assertions.assertTrue(suggestions.contains("tester")),
                () -> Assertions.assertTrue(suggestions.contains("tests"))
        );
    }

    @Test
    public void testGenerateSuggestionsForPerfectWord() {
        List<String> suggestions = engine.generateSuggestions("test").lines().toList();
        Assertions.assertTrue(suggestions.isEmpty(), "Suggestions should be empty for a perfect word");
    }
}
