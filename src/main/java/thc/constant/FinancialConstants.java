package thc.constant;

import com.mashape.unirest.http.HttpResponse;
import thc.parser.finance.EtnetIndexConstituentParser;
import thc.parser.finance.ISharesConstituentParser;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

/**
 * Created by thcathy on 17/7/2016.
 */
public class FinancialConstants {
    public enum Side {
        BUY("買入", 1), SELL("賣出", -1);

        final public String chinese;
        final public int factor;

        Side(String chinese, int factor) {
            this.chinese = chinese;
            this.factor = factor;
        }
    }

    public enum IndexCode {
        HSI("Hang Seng Index", "http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=HSI", EtnetIndexConstituentParser::parse),
        HSCEI("HS China Enterprises Index", "http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=cei", EtnetIndexConstituentParser::parse),
        HCCI("HS China Corp Index", "http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=cci", EtnetIndexConstituentParser::parse),
        MSCIChina("MSCI China Index", "https://www.blackrock.com/hk/en/terms-and-conditions?targetUrl=%2Fhk%2Fen%2Fproducts%2F251576%2Fishares-msci-china-index-etf%2F1440663547017.ajax%3Ftab%3Dall%26fileType%3Djson&action=ACCEPT", ISharesConstituentParser::parseMSCIChina),
        MSCIHK("MSCI HK Index", "https://www.ishares.com/us/products/239657/ishares-msci-hong-kong-etf/1467271812596.ajax?tab=all&fileType=json", ISharesConstituentParser::parseMSCIHK);

        final public String name;
        final public String url;
        final public Function<HttpResponse<InputStream>, List<String>> parser;

        IndexCode(String name, String url, Function<HttpResponse<InputStream>, List<String>> parser) {
            this.name = name;
            this.url = url;
            this.parser = parser;
        }
    }
}
