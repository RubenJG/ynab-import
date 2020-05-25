package com.rubenjg.ynab.dto.scotiabank;

public enum ScotiabankCellType {
    REFERENCE(0),
    DATE(1),
    DESCRIPTION(2),
    AMOUNT(3),
    CURRENCY(4),
    ENTRY_TYPE(5);

    private final int index;

    ScotiabankCellType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
