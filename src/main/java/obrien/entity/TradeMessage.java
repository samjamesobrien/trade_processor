package obrien.entity;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

/**
 * Representation of a trade message with validation.
 */
public interface TradeMessage {

    public int getUserId();

    public Currency getCurrencyFrom();

    public Currency getCurrencyTo();

    public int amountSell();

    public int amountBuy();

    public float rate();

    public Date timePlaced();

    public Locale originatingCountry();

}
