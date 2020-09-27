package com.rubenjg.ynab.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Cleared {
    @JsonProperty("cleared")
    CLEARED,
    @JsonProperty("uncleared")
    UNCLEARED,
    @JsonProperty("reconciled")
    RECONCILED
}
