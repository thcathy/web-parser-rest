package thc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.MonetaryBase;
import thc.domain.StockQuote;
import thc.parser.HttpParseRequest;
import thc.parser.finance.*;
import thc.service.HttpParseService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static thc.constant.FinancialConstants.IndexCode.HSCEI;
import static thc.constant.FinancialConstants.IndexCode.HSI;

@RestController
public class FinanceController {
	private static Logger log = LoggerFactory.getLogger(FinanceController.class);

	@Autowired
	HttpParseService parseService;

	enum StockQuoteSource {
		AASTOCKS(AastockStockQuoteRequest.class), ETNET(EtnetStockQuoteRequest.class);

		final public Class sourceClass;

		StockQuoteSource(Class sourceClass) {
			this.sourceClass = sourceClass;
		}
	}

    @RequestMapping(value = "/rest/quote/realtime/list/{codes}", method = GET)
    public List<StockQuote> hkQuotes(
    		@PathVariable String codes,
			@RequestParam(required = false) String source) {
    	log.info("hkquote: codes [{}]", codes);

		List<CompletableFuture<Optional<StockQuote>>> quotes = Arrays.stream(codes.split(","))
				.map(code -> queryStockQuote(code, source))
				.collect(Collectors.toList());

		return quotes.stream().map(q -> q.join())
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());
    }

	private CompletableFuture<Optional<StockQuote>> queryStockQuote(String code, String source) {
		try {
			Class stockQuoteClass = getStockQuoteClass(source);
			return parseService.process((HttpParseRequest<Optional<StockQuote>>) stockQuoteClass.getConstructors()[0].newInstance(code));
		} catch (Exception e) {
			log.error("Error in query stock quote for code: {} with source: {}", code, source, e);
			return CompletableFuture.completedFuture(Optional.empty());
		}
	}

	private Class getStockQuoteClass(String sourceStr) {
		try {
			return StockQuoteSource.valueOf(sourceStr.toUpperCase()).sourceClass;
		} catch (Exception e) {
			return StockQuoteSource.values()[(int) (System.currentTimeMillis() % StockQuoteSource.values().length)].sourceClass;
		}
	}

	@RequestMapping(value = "/rest/quote/full/{code}", method = GET)
	public StockQuote hkQuoteSingle(@PathVariable String code, @RequestParam(required = false) String source) {
		log.info("hkQuoteSingle: {}", code);

		//return ((Optional<StockQuote>) CompletableFuture.anyOf(quote).join()).get();

		CompletableFuture<Optional<StockQuote>> quoteFuture = queryStockQuote(code, source);
		List<CompletableFuture<Optional<BigDecimal>>> historyFutures = submitHistoryQuote(code);

		StockQuote quote = quoteFuture.join().get();
		for (int i=1; i<=3; i++) {
			quote.setPreviousPrice(i, historyFutures.get(i-1).join().orElse(new BigDecimal(0)).doubleValue());
		}

		return quote;
	}

	private List<CompletableFuture<Optional<BigDecimal>>> submitHistoryQuote(String code) {
		return IntStream.rangeClosed(1, 3)
				.mapToObj(i -> parseService.process(new HistoryQuoteRequest(code, i)))
				.collect(Collectors.toList());
	}

	@RequestMapping(value = "/rest/quote/indexes", method = GET)
	public List<StockQuote> indexQuotes() {
		log.info("request indexQuotes");
		return parseService.process(new Money18IndexQuoteRequest()).join();
	}

	@RequestMapping(value= "/rest/index/constituents/{index}", method = GET)
	public List<String> indexConstituents(@PathVariable String index) {
		log.info("request index constituents of {}", index);
		IndexCode indexCode = IndexCode.valueOf(index);

		return parseService.process(indexCode).join();
	}

	@RequestMapping(value = "/rest/index/report/hsinet/{yyyymmdd}", method = GET)
	public List<StockQuote> getHsiNetReports(@PathVariable String yyyymmdd) {
		log.info("request getHsiNetReportsClosestTo  [{}]", yyyymmdd);

		return Arrays.asList(
				    getIndexReport(HSI, yyyymmdd),
				    getIndexReport(HSCEI, yyyymmdd)
				);
	}

	@RequestMapping(value = "/rest/hkma/report/{yyyymmdd}", method = GET)
    public MonetaryBase getHKMAReport(@PathVariable String yyyymmdd) throws ParseException {
        log.info("request getHKMAReport [{}]", yyyymmdd);

        return parseService.process(new HKMAMonetaryBaseRequest(yyyymmdd))
				.join()
				.orElse(MonetaryBase.empty());
    }

    @RequestMapping(value = "/rest/quote/{code}/price/pre/{preYear}", method = GET)
	public BigDecimal getHistoryPrice(@PathVariable String code, @PathVariable int preYear) {
		return parseService.process(new HistoryQuoteRequest(code, preYear))
				.join()
				.orElse(new BigDecimal(0));
	}

	private StockQuote getIndexReport(IndexCode code, String yyyymmdd) {
	    HSINetRequest request = new HSINetRequest(code, yyyymmdd);
		return parseService.process(request)
                .join()
                .orElse(new StockQuote(StockQuote.NA));
	}

	public void setParseService(HttpParseService parseService) {
		this.parseService = parseService;
	}
}
