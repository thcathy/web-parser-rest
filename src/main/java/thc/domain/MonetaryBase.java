package thc.domain;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MonetaryBase {
	private double indebtedness = 0;
	private double notes = 0;
	private double closingBalance = 0;
	private double exchangeFund = 0;
	private double total = 0;
		
	public MonetaryBase() {}

	public MonetaryBase(double indebtedness, double notes,
                        double closingBalance, double exchangeFund) {
		this.indebtedness = indebtedness;
		this.notes = notes;
		this.closingBalance = closingBalance;
		this.exchangeFund = exchangeFund;
		this.total = indebtedness + notes + closingBalance + exchangeFund;
	}
	

	public double getIndebtedness() {
        return this.indebtedness;
    }

	public void setIndebtedness(double indebtedness) {
        this.indebtedness = indebtedness;
    }

	public double getNotes() {
        return this.notes;
    }

	public void setNotes(double notes) {
        this.notes = notes;
    }

	public double getClosingBalance() {
        return this.closingBalance;
    }

	public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

	public double getExchangeFund() {
        return this.exchangeFund;
    }

	public void setExchangeFund(double exchangeFund) {
        this.exchangeFund = exchangeFund;
    }

	public double getTotal() {
        return this.total;
    }

	public void setTotal(double total) {
        this.total = total;
    }

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
