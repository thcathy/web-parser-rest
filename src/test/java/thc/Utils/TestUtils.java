package thc.Utils;

import thc.domain.StockQuote;

import java.util.List;

/**
 * Created by wongtim on 22/07/2016.
 */
public class TestUtils {
    
    public static boolean containCode(String code, List<StockQuote> quotes) {
        return quotes.stream().anyMatch(q -> code.equals(q.getStockCode()));
    }
}
