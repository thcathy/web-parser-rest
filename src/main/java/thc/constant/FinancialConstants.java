package thc.constant;

import thc.parser.HttpParseRequest;
import thc.parser.finance.EtnetIndexConstituentParser;
import thc.parser.finance.ISharesConstituentParser;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;

/**
 * Created by thcathy on 17/7/2016.
 */
public class FinancialConstants {

    public enum IndexCode implements HttpParseRequest<List<String>> {
        HSI("Hang Seng Index", "http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=HSI", EtnetIndexConstituentParser::parse),
        HSCEI("HS China Enterprises Index", "http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=cei", EtnetIndexConstituentParser::parse),
        HCCI("HS China Corp Index", "http://www.etnet.com.hk/www/tc/stocks/indexes_detail.php?subtype=cci", EtnetIndexConstituentParser::parse),
        MSCIHK("MSCI HK Index", "https://www.ishares.com/us/products/239657/ishares-msci-hong-kong-etf/1467271812596.ajax?tab=all&fileType=json", ISharesConstituentParser::parseMSCIHK);

        final public String name;
        final public String url;
        final public Function<InputStream, List<String>> parser;

        IndexCode(String name, String url, Function<InputStream, List<String>> parser) {
            this.name = name;
            this.url = url;
            this.parser = parser;
        }

        @Override
        public String url() { return url; }

        @Override
        public List<String> parseResponse(InputStream response) {
            return parser.apply(response);
        }
    }
}
