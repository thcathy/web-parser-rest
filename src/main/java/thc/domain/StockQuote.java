package thc.domain;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import thc.util.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class StockQuote {
	public static String NA = "NA";

	private String stockCode = "";
	private String stockName = "";
	private String lastUpdate = NA;
	private String price = NA;
	private String high = NA;
	private String low = NA;
	private String change = NA;
	private String changeAmount = NA;
	private String pe = NA;
	private String yield = NA;
	private String NAV = NA;
	private String yearLow = NA;
	private String yearHigh = NA;

	private Double lastYearPercentage = null;
	private Double last2YearPercentage = null;
	private Double last3YearPercentage = null;

	private final Map<Integer, Double> previousPriceMap = new HashMap<>();

	public StockQuote() {}

	public StockQuote(String code) {
		this.stockCode = code;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public void setYearHigh(String yearHigh) {
		this.yearHigh = yearHigh;
	}

	public void setPreviousPrice(int previousYear, double price) {
		previousPriceMap.put(previousYear, price);
	}

	public Double getPreviousPrice(int previousYear) {
		return previousPriceMap.get(previousYear);
	}

	public double getPreviouYearPercentage(int previousYear) {
		double percentage = 0;
		try {
			double previousPrice = getPreviousPrice(previousYear);
			double realPrice = Double.valueOf(price);
			percentage = new BigDecimal(((realPrice - previousPrice) / previousPrice) * 100).setScale(2,RoundingMode.HALF_UP).doubleValue();
		} catch (Exception e) {
			percentage = 0.0;
		}
		return percentage;
	}

	public double getLastYearPercentage() {
		if (lastYearPercentage == null) {
			lastYearPercentage = getPreviouYearPercentage(1);
		}
		return lastYearPercentage;
	}


	public double getLast2YearPercentage() {
		if (last2YearPercentage == null) {
			last2YearPercentage = getPreviouYearPercentage(2);
		}
		return last2YearPercentage;
	}


	public double getLast3YearPercentage() {
		if (last3YearPercentage == null) {
			last3YearPercentage = getPreviouYearPercentage(3);
		}
		return last3YearPercentage;
	}

	public Double getPriceDoubleValue() { return NumberUtils.extractDouble(price); }

	@Override
	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

	public String getStockCode() {
        return this.stockCode;
    }

	public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

	public String getLastUpdate() {
        return this.lastUpdate;
    }

	public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

	public String getPrice() {
        return this.price;
    }

	public String getHigh() {
        return this.high;
    }

	public void setHigh(String high) {
        this.high = high;
    }

	public String getLow() {
        return this.low;
    }

	public void setLow(String low) {
        this.low = low;
    }

	public String getChange() {
        return this.change;
    }

	public void setChange(String change) {
        this.change = change;
    }

	public String getChangeAmount() {
        return this.changeAmount;
    }

	public void setChangeAmount(String changeAmount) {
        this.changeAmount = changeAmount;
    }

	public String getPe() {
        return this.pe;
    }

	public void setPe(String pe) {
        this.pe = pe;
    }

	public String getYield() {
        return this.yield;
    }

	public void setYield(String yield) {
        this.yield = yield;
    }

	public String getNAV() {
        return this.NAV;
    }

	public void setNAV(String NAV) {
        this.NAV = NAV;
    }

	public String getYearLow() {
        return this.yearLow;
    }

	public void setYearLow(String yearLow) {
        this.yearLow = yearLow;
    }

	public String getYearHigh() {
        return this.yearHigh;
    }

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
}
