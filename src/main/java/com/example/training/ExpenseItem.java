package com.example.training;

/**
 * 経費精算の1明細。
 *
 * @param category 費目（TRANSPORT: 交通費, MEAL: 食事代, OTHER: その他, LODGING: 宿泊費）
 * @param amount   金額（円）。0以上であること
 */
public record ExpenseItem(Category category, int amount) {

    public ExpenseItem {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be non-negative: " + amount);
        }
    }

    public enum Category {
        TRANSPORT, MEAL, OTHER, LODGING
    }
}
