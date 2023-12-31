package com.anw.managemymoney.enums;

public enum CategoryEnum {
    SHOPPING("shopping"),
    GROCERY("grocery"),
    VEGETABLES("vegetables"),
    MEATANDFISH("meatandfish"),
    RESTAURANTS("restaurants"),
    BILLS("bills"),
    TRAVEL("travel"),
    PETROL("petrol"),
    MEDICAL("medical"),
    OWN("own"),
    APARTMENT("apartment"),
    INVESTMENT("investment"),
    CREDITCARD("creditcard"),
    VEHICLE("vehicle"),
    ATM("atm"),
    GAS("gas"),
    DIVIDEND("dividend"),
    OTHERS("others");

    private final String displayName;

    CategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

