package thc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import thc.constant.FinancialConstants.IndexCode;
import thc.domain.StockQuote;
import thc.parser.JsoupParseRequest;
import thc.parser.finance.*;
import thc.service.HttpParseService;
import thc.service.JsoupParseService;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static thc.constant.FinancialConstants.IndexCode.HSCEI;
import static thc.constant.FinancialConstants.IndexCode.HSI;

@RestController
public class FinanceController {
	private static Logger log = LoggerFactory.getLogger(FinanceController.class);

	@Autowired
	HttpParseService parseService;

	@Autowired
	JsoupParseService jsoupParseService;

	enum StockQuoteSource {
		AASTOCKS(AastockStockQuoteRequest.class), MONEY18(Money18StockQuoteRequest.class);

		final public Class sourceClass;

		StockQuoteSource(Class sourceClass) {
			this.sourceClass = sourceClass;
		}
	}

	@RequestMapping(value = "/rest/quote/realtime/list/{codes}", method = GET)
	public Flux<StockQuote> hkQuotes(
			@PathVariable String codes,
			@RequestParam(required = false) String source) {
		log.info("hkquote: codes [{}]", codes);

		return Flux.fromArray(codes.split(","))
				.flatMap(code -> queryStockQuote(code, source));
	}

	private Mono<StockQuote> queryStockQuote(String code, String source) {
		try {
			Class stockQuoteClass = getStockQuoteClass(source);
			var stockQuoteInstance = stockQuoteClass.getConstructors()[0].newInstance(code);
			if (stockQuoteInstance instanceof JsoupParseRequest) {
				return jsoupParseService.process((JsoupParseRequest<StockQuote>) stockQuoteInstance);
			} else if (stockQuoteInstance instanceof Money18StockQuoteRequest){
				return parseService.processFlux((Money18StockQuoteRequest) stockQuoteInstance);
			}
			throw new RuntimeException("Cannot cast stock quote class: " + stockQuoteClass.toString());
		} catch (Exception e) {
			log.error("Error in query stock quote for code: {} with source: {}", code, source, e);
			return Mono.empty();
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
	public Mono<StockQuote> hkQuoteSingle(@PathVariable String code, @RequestParam(required = false) String source) {
		log.info("hkQuoteSingle: {}", code);

		Mono<StockQuote> quote = queryStockQuote(code, source);
		Mono<Map<Integer, Mono<BigDecimal>>> historyQuotes = submitHistoryQuote(code);

		return quote.zipWith(historyQuotes, this::setHistoryQuotes);
	}

	private StockQuote setHistoryQuotes(StockQuote stockQuote, Map<Integer, Mono<BigDecimal>> historyQuotes) {
		historyQuotes.forEach((i, v) -> stockQuote.setPreviousPrice(i, v.block().doubleValue()));
		return stockQuote;
	}

	private Mono<Map<Integer, Mono<BigDecimal>>> submitHistoryQuote(String code) {
		return Flux.range(1, 3)
			.collectMap(
				i -> i,
				i -> parseService.processFlux(new SingleDateHistoryQuoteRequest(code, i))
			);
	}

	@RequestMapping(value = "/rest/quote/indexes", method = GET)
	public Mono<List<StockQuote>> indexQuotes() {
		log.info("request indexQuotes");
		var worldIndexes = parseService.processFlux(new Money18WorldIndexQuoteRequest());
		var localIndexes = parseService.processFlux(new Money18LocalIndexQuoteRequest());
		return worldIndexes.zipWith(
				localIndexes,
				(a, b) -> {
					a.addAll(b);
					return a;
		});
	}

	@RequestMapping(value= "/rest/index/constituents/{index}", method = GET)
	public Mono<List<String>> indexConstituents(@PathVariable String index) {
		log.info("request index constituents of {}", index);
		IndexCode indexCode = IndexCode.valueOf(index);

		return parseService.processFlux(indexCode);
	}

	@RequestMapping(value = "/rest/index/report/hsinet/{yyyymmdd}", method = GET)
	public Flux<StockQuote> getHsiNetReports(@PathVariable String yyyymmdd) {
		log.info("request getHsiNetReportsClosestTo  [{}]", yyyymmdd);

		return Flux.concat(getIndexReport(HSI, yyyymmdd), getIndexReport(HSCEI, yyyymmdd));
	}
	
	@RequestMapping(value = "/rest/quote/{code}/price/pre/{preYear}", method = GET)
	public Mono<BigDecimal> getHistoryPrice(@PathVariable String code, @PathVariable int preYear) {
		return parseService.processFlux(new SingleDateHistoryQuoteRequest(code, preYear));
	}

	@GetMapping("/rest/quote/{code}/range/{fromDate}/{toDate}")
	public Mono<List<DailyStockQuote>> getQuotesInRange(@PathVariable String code,
												   @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date fromDate,
												   @PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date toDate) {
		Calendar calendarFromDate = Calendar.getInstance();
        calendarFromDate.setTime(fromDate);

        Calendar calendarToDate = Calendar.getInstance();
        calendarToDate.setTime(toDate);

		return parseService.processFlux(new RangeHistoryQuoteRequest(code,  calendarFromDate, calendarToDate));
	}

	private Mono<StockQuote> getIndexReport(IndexCode code, String yyyymmdd) {
		HSINetRequest request = new HSINetRequest(code, yyyymmdd);
		return parseService.processFlux(request)
				.map(Optional::get)
				.onErrorReturn(new StockQuote(StockQuote.NA));
	}

	public void setParseService(HttpParseService parseService) {
		this.parseService = parseService;
	}

	public FinanceController setJsoupParseService(JsoupParseService jsoupParseService) {
		this.jsoupParseService = jsoupParseService;
		return this;
	}
}
