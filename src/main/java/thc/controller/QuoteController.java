package thc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.StockQuote;
import thc.parser.finance.*;
import thc.service.HttpService;
import thc.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static thc.constant.FinancialConstants.IndexCode.HSCEI;
import static thc.constant.FinancialConstants.IndexCode.HSI;

@RestController
public class QuoteController {
	private static Logger log = LoggerFactory.getLogger(QuoteController.class);

	@Autowired
	HttpService httpService;

    @RequestMapping(value = "/rest/quote/realtime/list/{codes}")
    public List<StockQuote> hkQuotes(@PathVariable String codes) {
    	log.debug("hkquote: codes [{}]", codes);

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
		log.debug("hkQuoteSingle: {}", code);

		CompletableFuture<Optional<StockQuote>> quote = httpService.queryAsync(EtnetStockQuoteParser.createRequest(code)::asBinaryAsync, EtnetStockQuoteParser::parse);
		CompletableFuture<Optional<StockQuote>> quote2 = httpService.queryAsync(AastockStockQuoteParser.createRequest(code)::asBinaryAsync, AastockStockQuoteParser::parse);

		return ((Optional<StockQuote>) CompletableFuture.anyOf(quote, quote2).join()).get();
	}

	@RequestMapping(value = "/rest/quote/indexes")
	public List<StockQuote> indexQuotes() {
		log.debug("request indexQuotes");
		return httpService.queryAsync(Money18IndexQuoteParser.createRequest()::asStringAsync, Money18IndexQuoteParser::parse).join();
	}

	@RequestMapping(value= "/rest/index/constituents/{index}")
	public List<String> indexConstituents(@PathVariable String index) {
		log.debug("request index constituents of {}", index);
		IndexCode indexCode = IndexCode.valueOf(index);

		return httpService.getAsync(indexCode.url, indexCode.parser).join();
	}

	@RequestMapping(value = "/rest/index/report/hsinet/{yyyymmdd}")
	public List<StockQuote> getHsiNetReportsClosestTo(@PathVariable String yyyymmdd) throws ParseException {
		log.debug("request getHsiNetReportsClosestTo  [{}]", yyyymmdd);

		return Arrays.asList(
				getLatestIndexReport(HSI, yyyymmdd),
				getLatestIndexReport(HSCEI, yyyymmdd)
				);
	}

	private StockQuote getLatestIndexReport(IndexCode code, String yyyymmdd) throws ParseException {
		return tenPreviousDayStreamFrom(yyyymmdd)
				.filter(DateUtils::notWeekEnd)
				.map(Calendar::getTime)
				.map(t -> new HSINetParser(code, t))
				.map(this::submitHttpRequest)
				.map(CompletableFuture::join)
				.filter(quote -> quote.isPresent() && quote.get().getPriceDoubleValue() > 1)
				.findFirst().get().get();
	}

	private CompletableFuture<Optional<StockQuote>> submitHttpRequest(HSINetParser r) {
		return httpService
				.queryAsync(
						r.createRequest()::asStringAsync,
						r::parse);
	}

	private Stream<Calendar> tenPreviousDayStreamFrom(String yyyymmdd) throws ParseException {
		Date date = new SimpleDateFormat("yyyyMMdd").parse(yyyymmdd);
		return IntStream.range(0, 10).mapToObj(i-> {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(Calendar.DATE, -i-1);
			return c;
		});
	}
}
