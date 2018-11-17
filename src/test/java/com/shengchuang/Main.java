package com.shengchuang;

import java.util.Observable;

public class Main {

    public static String[] markets = {"BTC/QC", "LTC/QC", "ETH/QC", "USDT/QC",};
    public static String[] names = {"BTC", "LTC", "ETH", "USDT"};

    public static void main(String[] args) {
        ObservableX observable = new ObservableX();
        observable.setChanged();
        observable.addObserver((o, arg) -> {
            System.out.println("???");
        });

        observable.notifyObservers();
    }

    static class ObservableX extends Observable {
        @Override
        protected synchronized void setChanged() {
            super.setChanged();
        }
    }
}
