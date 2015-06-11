package obrien.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

/**
 * Representation of a trade message with validation.
 */
@Entity
@Table(name = "tradeMessages")
public class TradeMessage {

    private int id;
    private int userId;
    private Currency currencyFrom;
    private Currency currencyTo;
    private BigDecimal amountSell;
    private BigDecimal amountBuy;
    private BigDecimal rate;
    private Date timePlaced;
    private Locale originatingCountry;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    @Min(0)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NotNull
    public Currency getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(Currency currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    @NotNull
    public Currency getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(Currency currencyTo) {
        this.currencyTo = currencyTo;
    }

    @NotNull
    @Min(0)
    public BigDecimal getAmountSell() {
        return amountSell;
    }

    public void setAmountSell(BigDecimal amountSell) {
        this.amountSell = amountSell;
    }

    @NotNull
    @Min(0)
    public BigDecimal getAmountBuy() {
        return amountBuy;
    }

    public void setAmountBuy(BigDecimal amountBuy) {
        this.amountBuy = amountBuy;
    }

    @NotNull
    @Min(0)
    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @NotNull
    public Date getTimePlaced() {
        return timePlaced;
    }

    public void setTimePlaced(Date timePlaced) {
        this.timePlaced = timePlaced;
    }

    @NotNull
    public Locale getOriginatingCountry() {
        return originatingCountry;
    }

    public void setOriginatingCountry(Locale originatingCountry) {
        this.originatingCountry = originatingCountry;
    }
}
