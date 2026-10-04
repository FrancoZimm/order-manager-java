import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.model.Article;

public class OrderTestCase {

    @Test
    void testGetGrossAmount() {
        Article article = new Article("Lapiz", 3, 10.0, 20.0);
        assertEquals(30.0, article.getGrossAmount());
    }

    @Test
    void testGetDiscountedAmount() {
        Article article = new Article("Cuaderno", 2, 50.0, 10.0);
        assertEquals(90.0, article.getDiscountedAmount());
    }

    @Test
    void testSettersAndGetters() {
        Article article = new Article("Borrador", 1, 5.0, 0.0);
        article.setName("Goma");
        article.setQuantity(2);
        article.setPrice(3.0);
        article.setDiscount(5.0);

        assertEquals("Goma", article.getName());
        assertEquals(2, article.getQuantity());
        assertEquals(3.0, article.getPrice());
        assertEquals(5.0, article.getDiscount());
    }

    @Test
    void testToString() {
        Article article = new Article("Regla", 1, 15.0, 10.0);
        String str = article.toString();
        assertNotNull(str);
        assertEquals(true, str.contains("Regla"));
        assertEquals(true, str.contains("grossAmount"));
        assertEquals(true, str.contains("discountedAmount"));
    }

    @Test
    @DisplayName("Test: default constructor initializes defaults")
    void testArticleDefaultConstructor() {
        Article a = new Article();
        assertNotNull(a);
        assertEquals(null, a.getName());
        assertEquals(0, a.getQuantity());
        assertEquals(0.0, a.getPrice(), 1e-9);
        assertEquals(0.0, a.getDiscount(), 1e-9);

        assertEquals(0.0, a.getGrossAmount(), 1e-9);
        assertEquals(0.0, a.getDiscountedAmount(), 1e-9);

        String s = a.toString();
        assertNotNull(s);
    }
}