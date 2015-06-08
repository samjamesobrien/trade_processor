package obrien.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

/**
 * Representation of a trade message with validation.
 */
public class TradeMessage {

    @NotNull
    private int userId;
    @NotNull
    private Currency currencyFrom;
    @NotNull
    private Currency currencyTo;
    @NotNull
    @Min(0)
    private BigDecimal amountSell;
    @NotNull
    @Min(0)
    private BigDecimal amountBuy;
    @NotNull
    @Min(0)
    private BigDecimal rate;
    @NotNull
    private Date timePlaced;
    @NotNull
    private Locale originatingCountry;

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCurrencyFrom(Currency currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public void setCurrencyTo(Currency currencyTo) {
        this.currencyTo = currencyTo;
    }

    public void setAmountSell(BigDecimal amountSell) {
        this.amountSell = amountSell;
    }

    public void setAmountBuy(BigDecimal amountBuy) {
        this.amountBuy = amountBuy;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setTimePlaced(Date timePlaced) {
        this.timePlaced = timePlaced;
    }

    public void setOriginatingCountry(Locale originatingCountry) {
        this.originatingCountry = originatingCountry;
    }


    public int getUserId() {
        return userId;
    }

    public Currency getCurrencyFrom() {
        return currencyFrom;
    }

    public Currency getCurrencyTo() {
        return currencyTo;
    }

    public BigDecimal amountSell() {
        return amountSell;
    }

    public BigDecimal amountBuy() {
        return amountBuy;
    }

    public BigDecimal rate() {
        return rate;
    }

    public Date timePlaced() {
        return timePlaced;
    }

    public Locale originatingCountry() {
        return originatingCountry;
    }


}
