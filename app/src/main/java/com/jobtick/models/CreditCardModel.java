package com.jobtick.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class CreditCardModel implements  Parcelable{

    private boolean success;
    private String message;
    private List<Data> data;

    public static class Data implements Parcelable {

        private Card card;
        private Wallet wallet;

        protected Data(Parcel in) {
            card = in.readParcelable(Card.class.getClassLoader());
            wallet = in.readParcelable(Wallet.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(card, flags);
            dest.writeParcelable(wallet, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public Wallet getWallet() {
            return wallet;
        }

        public void setWallet(Wallet wallet) {
            this.wallet = wallet;
        }
    }

    public static class Card implements Parcelable {

        private String brand;
        private String last4;
        private int exp_month;
        private int exp_year;

        protected Card(Parcel in) {
            brand = in.readString();
            last4 = in.readString();
            exp_month = in.readInt();
            exp_year = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(brand);
            dest.writeString(last4);
            dest.writeInt(exp_month);
            dest.writeInt(exp_year);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Card> CREATOR = new Creator<Card>() {
            @Override
            public Card createFromParcel(Parcel in) {
                return new Card(in);
            }

            @Override
            public Card[] newArray(int size) {
                return new Card[size];
            }
        };

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getLast4() {
            return last4;
        }

        public void setLast4(String last4) {
            this.last4 = last4;
        }

        public int getExp_month() {
            return exp_month;
        }

        public void setExp_month(int exp_month) {
            this.exp_month = exp_month;
        }

        public int getExp_year() {
            return exp_year;
        }

        public void setExp_year(int exp_year) {
            this.exp_year = exp_year;
        }
    }
    public static class Wallet implements Parcelable{

        private int balance;

        protected Wallet(Parcel in) {
            balance = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(balance);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
            @Override
            public Wallet createFromParcel(Parcel in) {
                return new Wallet(in);
            }

            @Override
            public Wallet[] newArray(int size) {
                return new Wallet[size];
            }
        };

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }
    }

    protected CreditCardModel(Parcel in) {
        success = in.readByte() != 0;
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success ? 1 : 0));
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CreditCardModel> CREATOR = new Creator<CreditCardModel>() {
        @Override
        public CreditCardModel createFromParcel(Parcel in) {
            return new CreditCardModel(in);
        }

        @Override
        public CreditCardModel[] newArray(int size) {
            return new CreditCardModel[size];
        }
    };

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}

