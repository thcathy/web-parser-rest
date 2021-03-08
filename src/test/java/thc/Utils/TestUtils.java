package thc.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thc.domain.StockQuote;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wongtim on 22/07/2016.
 */
public class TestUtils {
    private static Logger log = LoggerFactory.getLogger(TestUtils.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
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

    public static String getOneCBBCCodeFromHKEX() throws Exception {
        InputStream input = new URL("https://www.hkex.com.hk/eng/cbbc/search/cbbcFullList.csv").openStream();

        var reader = new InputStreamReader(input, "UTF-16");
        var bufferedReader = new BufferedReader(reader);
        Optional<String> cbbc = Optional.empty();
        while (!cbbc.isPresent()) {
            cbbc = isListedCBBC(bufferedReader.readLine());
        }
        log.debug("CBBC found: {}", cbbc);
        return cbbc.get().split("\\s+")[0];
    }

    private static Optional<String> isListedCBBC(String data) {
        log.debug("Check is listed CBBC: {}", data);
        long now = new Date().getTime();
        try {
            var dataArray = data.split("\\s+");
            var date = dateFormat.parse(dataArray[6]);
            if (date.getTime() < now) {
                return Optional.of(data);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
