package obrien.processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.EvictingQueue;
import obrien.Util.DefaultValues;
import obrien.entity.TradeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static obrien.Util.DefaultValues.*;

/**
 * Track overall metrics for trades.
 * <p>Doesn't do much yet.</p>
 */
public class Trends {

    // This could be something much more interesting, but I am low on time
    private static final Map<Currency, EvictingQueue<BigDecimal>> trends = new ConcurrentHashMap<>();

    /**
     * Submit a trade message for metrics extraction.
     * @param tradeMessage a single trade.
     */
    public static void submitTrade(TradeMessage tradeMessage) {
        // For the currency sold, update the last recent trades
        Currency soldCurrency = tradeMessage.getCurrencyFrom();
        EvictingQueue<BigDecimal> soldRecents = trends.getOrDefault(
                soldCurrency, EvictingQueue.create(LAST_N_TRADES));
        soldRecents.add(tradeMessage.getAmountSell().negate()); // a critical difference, .negate()
        trends.put(soldCurrency, soldRecents);

        // For the currency bought, update the last recent trades
        Currency boughtCurrency = tradeMessage.getCurrencyTo();
        EvictingQueue<BigDecimal> boughtRecents = trends.getOrDefault(
                boughtCurrency, EvictingQueue.create(LAST_N_TRADES));
        boughtRecents.add(tradeMessage.getAmountBuy());
        trends.put(boughtCurrency, boughtRecents);
    }

    /**
     * What are the totals bought - sold for the currencies?
     * @return map of totals for the currencies.
     */
    public static Map<Currency, BigDecimal> calculateTotals() {
        HashMap<Currency, BigDecimal> output = new HashMap<>();

        trends.keySet().forEach((key) -> {
            BigDecimal total = trends.get(key).stream().reduce((a, b) -> a.add(b)).get();
            output.put(key, total);
        });
        return output;
    }
}
