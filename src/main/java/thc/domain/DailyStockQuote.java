package thc.domain;

import java.time.LocalDate;

public record DailyStockQuote(LocalDate date, double open, double close, double high, double low, int volume, double adjClose) {}
