package systems;

import config.GameConfig;

public class ScoringSystem {
    private int totalMoney;
    private int correctServes;
    private int wrongServes;
    private int angryCustomers;
    private int trashedItems;

    public ScoringSystem() {
        reset();
    }

    public void reset() {
        totalMoney = 0;
        correctServes = 0;
        wrongServes = 0;
        angryCustomers = 0;
        trashedItems = 0;
    }

    public void addCorrectServe() {
        correctServes++;
        totalMoney += GameConfig.MONEY_PER_CORRECT_ORDER;
    }

    public void addWrongServe() {
        wrongServes++;
        totalMoney += GameConfig.MONEY_PENALTY_WRONG;
    }

    public void addAngryCustomer() {
        angryCustomers++;
        totalMoney += GameConfig.MONEY_PENALTY_ANGRY;
    }

    public void addTrashedItem() {
        trashedItems++;
        totalMoney += GameConfig.MONEY_PENALTY_TRASH;
    }

    // Getters
    public int getTotalMoney() { return totalMoney; }
    public int getCorrectServes() { return correctServes; }
    public int getWrongServes() { return wrongServes; }
    public int getAngryCustomers() { return angryCustomers; }
    public int getTrashedItems() { return trashedItems; }
}