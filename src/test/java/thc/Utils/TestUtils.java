package thc.Utils;

import thc.domain.StockQuote;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by wongtim on 22/07/2016.
 */
public class TestUtils {
    
    public static boolean containCode(String code, List<StockQuote> quotes) {
        return quotes.stream().anyMatch(q -> code.equals(q.getStockCode()));
    }

    public static boolean withIntraDayData() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Hong_Kong"));
        if (c.get(Calendar.HOUR_OF_DAY) < 9 || c.get(Calendar.MINUTE) < 20)
            return false;
        else if (c.get(Calendar.HOUR_OF_DAY) > 22)
            return false;
        else
            return true;
    }
}
