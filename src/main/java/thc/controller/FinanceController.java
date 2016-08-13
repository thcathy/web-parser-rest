package thc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.MonetaryBase;
import thc.domain.StockQuote;
import thc.parser.finance.*;
import thc.service.HttpService;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static thc.constant.FinancialConstants.IndexCode.HSCEI;
import static thc.constant.FinancialConstants.IndexCode.HSI;

@RestController
public class FinanceController {
	private static Logger log = LoggerFactory.getLogger(FinanceController.class);

	@Autowired
	HttpService httpService;

    @RequestMapping(value = "/rest/quote/realtime/list/{codes}")
    public List<StockQuote> hkQuotes(@PathVariable String codes) {
    	log.info("hkquote: codes [{}]", codes);

		List<CompletableFuture<Optional<StockQuote>>> quotes = Arrays.stream(codes.split(","))
				.map(SinaStockQuoteParser::createRequest)
                .map(r -> httpService.queryAsync(r::asBinaryAsync, SinaStockQuoteParser::parse))
				.collect(Collectors.toList());

		return quotes.stream().map(q -> q.join())
                .filter(Optional::isPresent)
                .map(Optional::get)
				.collect(Collectors.toList());
    }

    @RequestMapping(value = "/rest/quote/full/{code}")
	public StockQuote hkQuoteSingle(@PathVariable String code) {
		log.info("hkQuoteSingle: {}", code);

		CompletableFuture<Optional<StockQuote>> quote = httpService.queryAsync(EtnetStockQuoteParser.createRequest(code)::asBinaryAsync, EtnetStockQuoteParser::parse);
		CompletableFuture<Optional<StockQuote>> quote2 = httpService.queryAsync(AastockStockQuoteParser.createRequest(code)::asBinaryAsync, AastockStockQuoteParser::parse);

		return ((Optional<StockQuote>) CompletableFuture.anyOf(quote, quote2).join()).get();
	}

	@RequestMapping(value = "/rest/quote/indexes")
	public List<StockQuote> indexQuotes() {
		log.info("request indexQuotes");
		return httpService.queryAsync(Money18IndexQuoteParser.createRequest()::asStringAsync, Money18IndexQuoteParser::parse).join();
	}

	@RequestMapping(value= "/rest/index/constituents/{index}")
	public List<String> indexConstituents(@PathVariable String index) {
		log.info("request index constituents of {}", index);
		IndexCode indexCode = IndexCode.valueOf(index);

		return httpService.getAsync(indexCode.url, indexCode.parser).join();
	}

	@RequestMapping(value = "/rest/index/report/hsinet/{yyyymmdd}")
	public List<StockQuote> getHsiNetReports(@PathVariable String yyyymmdd) throws ParseException {
		log.info("request getHsiNetReportsClosestTo  [{}]", yyyymmdd);

		return Arrays.asList(
				    getIndexReport(HSI, yyyymmdd),
				    getIndexReport(HSCEI, yyyymmdd)
				);
	}

	@RequestMapping(value = "/rest/hkma/report/{yyyymmdd}")
    public MonetaryBase getHKMAReport(@PathVariable String yyyymmdd) throws ParseException {
        log.info("request getHKMAReport [{}]", yyyymmdd);

        HKMAMonetaryBaseParser parser = new HKMAMonetaryBaseParser(yyyymmdd);
        return httpService
                .getAsync(parser.url(), parser::parse)
                .join()
                .orElse(MonetaryBase.empty());
    }

	private StockQuote getIndexReport(IndexCode code, String yyyymmdd) throws ParseException {
	    HSINetParser parser = new HSINetParser(code, yyyymmdd);
		return httpService
                .queryAsync(parser.createRequest()::asStringAsync,parser::parse)
                .join()
                .orElse(new StockQuote(StockQuote.NA));
	}

}
