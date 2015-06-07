package obrien.entity;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    private int amountSell;
    @NotNull
    @Min(0)
    private int amountBuy;
    @NotNull
    @Min(0)
    private float rate;
    @NotNull//24­JAN­15 10:27:44
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

    public void setAmountSell(int amountSell) {
        this.amountSell = amountSell;
    }

    public void setAmountBuy(int amountBuy) {
        this.amountBuy = amountBuy;
    }

    public void setRate(float rate) {
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

    public int amountSell() {
        return amountSell;
    }

    public int amountBuy() {
        return amountBuy;
    }

    public float rate() {
        return rate;
    }

    public Date timePlaced() {
        return timePlaced;
    }

    public Locale originatingCountry() {
        return originatingCountry;
    }


}
