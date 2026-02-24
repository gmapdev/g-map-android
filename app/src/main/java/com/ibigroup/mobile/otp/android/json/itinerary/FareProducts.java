package com.ibigroup.mobile.otp.android.json.itinerary;

import java.io.Serializable;

public class FareProducts implements Serializable {
    private String id;
    private Product product;

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public class Product implements Serializable {
        private String __typename;
        private String id;
        private Medium medium;
        private String name;
        private RiderCategory riderCategory;
        private Price price;

        public String get__typename() {
            return __typename;
        }

        public String getId() {
            return id;
        }

        public Medium getMedium() {
            return medium;
        }

        public String getName() {
            return name;
        }

        public RiderCategory getRiderCategory() {
            return riderCategory;
        }

        public Price getPrice() {
            return price;
        }
    }

    public class Medium implements Serializable {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class RiderCategory implements Serializable {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class Price implements Serializable {
        private double amount;
        private Currency currency;

        public double getAmount() {
            return amount;
        }

        public Currency getCurrency() {
            return currency;
        }
    }

    public class Currency implements Serializable {
        private String code;
        private int digits;

        public String getCode() {
            return code;
        }

        public int getDigits() {
            return digits;
        }
    }

}




