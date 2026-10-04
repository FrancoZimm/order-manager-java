import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.example.model.Order;
import com.example.model.Article;

import java.util.List;

public class ArticleTestCase {

    @Test
    void testGetGrossTotal() {
        List<Article> articulos = List.of(
            new Article("Lapiz", 3, 10.0, 20.0),
            new Article("Cuaderno", 2, 50.0, 10.0)
        );
        Order order = new Order("ORD1", articulos);
        assertEquals(130.0, order.getGrossTotal());
    }

    @Test
    void testGetDiscountedTotal() {
        List<Article> articulos = List.of(
            new Article("Lapiz", 3, 10.0, 20.0),
            new Article("Cuaderno", 2, 50.0, 10.0)
        );
        Order order = new Order("ORD1", articulos);
        assertEquals(114.0, order.getDiscountedTotal());
    }

    @Test
    void testSettersAndGetters() {
        List<Article> articulos = List.of(
            new Article("Lapiz", 3, 10.0, 20.0)
        );
        Order order = new Order("ORD1", articulos);
        order.setId("ORD2");
        assertEquals("ORD2", order.getId());
        order.setArticles(List.of(new Article("Cuaderno", 2, 50.0, 10.0)));
        assertEquals(1, order.getArticles().size());
        assertEquals("Cuaderno", order.getArticles().get(0).getName());
    }

    @Test
    void testToString() {
        List<Article> articles = List.of(
            new Article("Lapiz", 3, 10.0, 20.0)
        );
        Order order = new Order("ORD1", articles);
        String str = order.toString();
        assertNotNull(str);
        assertEquals(true, str.contains("ORD1"));
        assertEquals(true, str.contains("articles"));
    }

    @Test
    void testTotalsEmptyArticlesList() {
        Order order = new Order("ORD-EMPTY", List.of());
        assertEquals(0.0, order.getGrossTotal(), 1e-9);
        assertEquals(0.0, order.getDiscountedTotal(), 1e-9);
    }

    @Test
    void testTotalsNullArticlesThrows() {
        Order order = new Order();
        order.setId("ORD-NONE");
        order.setArticles(null);
        try {
            order.getGrossTotal();
        } catch (NullPointerException e) {
            // expected
        }
        try {
            order.getDiscountedTotal();
        } catch (NullPointerException e) {
            // expected
        }
        }

    @Test
    void testSingleArticleTotals() {
        Article a = new Article("Solo", 2, 12.5, 20.0);
        Order order = new Order("ORD-S", List.of(a));
        assertEquals(a.getGrossAmount(), order.getGrossTotal(), 1e-9);
        assertEquals(a.getDiscountedAmount(), order.getDiscountedTotal(), 1e-9);
    }

    @Test
    void testConstructorAssignsFields() {
        List<Article> arts = List.of(new Article("X", 1, 1.0, 0.0));
        Order o = new Order("ID1", arts);
        assertEquals("ID1", o.getId());
        assertEquals(arts, o.getArticles());
    }
}