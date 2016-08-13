package thc.domain;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MonetaryBase {
	public final double indebtedness;
	public final double notes;
	public final double closingBalance;
	public final double exchangeFund;
	public final double total;

	public MonetaryBase(double indebtedness, double notes,
                        double closingBalance, double exchangeFund) {
		this.indebtedness = indebtedness;
		this.notes = notes;
		this.closingBalance = closingBalance;
		this.exchangeFund = exchangeFund;
		this.total = indebtedness + notes + closingBalance + exchangeFund;
	}

	public static MonetaryBase empty() {
		return new MonetaryBase(0,0,0,0);
	}

	public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
