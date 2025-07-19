package com.imeth.imexbank.web.utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class JSPHelper {

    /**
     * Formats a BigDecimal value into a currency string.
     * @param amount The BigDecimal amount.
     * @return A formatted currency string (e.g., "\$1,234.56").
     */
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormatter.format(amount);
    }
}