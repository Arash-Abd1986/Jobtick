package com.jobtick.models;

import java.util.List;

public class CreditCardModel {


    /**
     * success : true
     * message : success
     * data : {"balance":"0","card":{"brand":"visa","checks":{"address_line1_check":null,"address_postal_code_check":null,"cvc_check":"pass"},"country":"US","exp_month":4,"exp_year":2026,"fingerprint":"mntOtik3A1oJyzit","funding":"credit","generated_from":null,"last4":"4242","networks":{"available":["visa"],"preferred":null},"three_d_secure_usage":{"supported":true},"wallet":null}}
     */

    private boolean success;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * balance : 0
         * card : {"brand":"visa","checks":{"address_line1_check":null,"address_postal_code_check":null,"cvc_check":"pass"},"country":"US","exp_month":4,"exp_year":2026,"fingerprint":"mntOtik3A1oJyzit","funding":"credit","generated_from":null,"last4":"4242","networks":{"available":["visa"],"preferred":null},"three_d_secure_usage":{"supported":true},"wallet":null}
         */

        private String balance;
        private CardBean card;

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public CardBean getCard() {
            return card;
        }

        public void setCard(CardBean card) {
            this.card = card;
        }

        public static class CardBean {
            /**
             * brand : visa
             * checks : {"address_line1_check":null,"address_postal_code_check":null,"cvc_check":"pass"}
             * country : US
             * exp_month : 4
             * exp_year : 2026
             * fingerprint : mntOtik3A1oJyzit
             * funding : credit
             * generated_from : null
             * last4 : 4242
             * networks : {"available":["visa"],"preferred":null}
             * three_d_secure_usage : {"supported":true}
             * wallet : null
             */

            private String brand;
            private ChecksBean checks;
            private String country;
            private int exp_month;
            private int exp_year;
            private String fingerprint;
            private String funding;
            private Object generated_from;
            private String last4;
            private NetworksBean networks;
            private ThreeDSecureUsageBean three_d_secure_usage;
            private Object wallet;

            public String getBrand() {
                return brand;
            }

            public void setBrand(String brand) {
                this.brand = brand;
            }

            public ChecksBean getChecks() {
                return checks;
            }

            public void setChecks(ChecksBean checks) {
                this.checks = checks;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
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

            public String getFingerprint() {
                return fingerprint;
            }

            public void setFingerprint(String fingerprint) {
                this.fingerprint = fingerprint;
            }

            public String getFunding() {
                return funding;
            }

            public void setFunding(String funding) {
                this.funding = funding;
            }

            public Object getGenerated_from() {
                return generated_from;
            }

            public void setGenerated_from(Object generated_from) {
                this.generated_from = generated_from;
            }

            public String getLast4() {
                return last4;
            }

            public void setLast4(String last4) {
                this.last4 = last4;
            }

            public NetworksBean getNetworks() {
                return networks;
            }

            public void setNetworks(NetworksBean networks) {
                this.networks = networks;
            }

            public ThreeDSecureUsageBean getThree_d_secure_usage() {
                return three_d_secure_usage;
            }

            public void setThree_d_secure_usage(ThreeDSecureUsageBean three_d_secure_usage) {
                this.three_d_secure_usage = three_d_secure_usage;
            }

            public Object getWallet() {
                return wallet;
            }

            public void setWallet(Object wallet) {
                this.wallet = wallet;
            }

            public static class ChecksBean {
                /**
                 * address_line1_check : null
                 * address_postal_code_check : null
                 * cvc_check : pass
                 */

                private Object address_line1_check;
                private Object address_postal_code_check;
                private String cvc_check;

                public Object getAddress_line1_check() {
                    return address_line1_check;
                }

                public void setAddress_line1_check(Object address_line1_check) {
                    this.address_line1_check = address_line1_check;
                }

                public Object getAddress_postal_code_check() {
                    return address_postal_code_check;
                }

                public void setAddress_postal_code_check(Object address_postal_code_check) {
                    this.address_postal_code_check = address_postal_code_check;
                }

                public String getCvc_check() {
                    return cvc_check;
                }

                public void setCvc_check(String cvc_check) {
                    this.cvc_check = cvc_check;
                }
            }

            public static class NetworksBean {
                /**
                 * available : ["visa"]
                 * preferred : null
                 */

                private Object preferred;
                private List<String> available;

                public Object getPreferred() {
                    return preferred;
                }

                public void setPreferred(Object preferred) {
                    this.preferred = preferred;
                }

                public List<String> getAvailable() {
                    return available;
                }

                public void setAvailable(List<String> available) {
                    this.available = available;
                }
            }

            public static class ThreeDSecureUsageBean {
                /**
                 * supported : true
                 */

                private boolean supported;

                public boolean isSupported() {
                    return supported;
                }

                public void setSupported(boolean supported) {
                    this.supported = supported;
                }
            }
        }
    }
}
