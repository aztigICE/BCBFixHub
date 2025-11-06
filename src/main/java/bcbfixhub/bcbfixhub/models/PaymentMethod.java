package bcbfixhub.bcbfixhub.models;

import java.time.LocalDate;

public class PaymentMethod {
    private int paymentMethodId;
    private int userId;
    private String type;
    private String provider;
    private String accountNumber;
    private LocalDate expiryDate;
    private String billingAddress;
    private boolean isDefault;

    public PaymentMethod() {}

    public PaymentMethod(int paymentMethodId,
                         int userID,
                         String type,
                         String provider,
                         String accountNumber,
                         LocalDate expiryDate,
                         String billingAddress,
                         boolean isDefault) {
        this.paymentMethodId = paymentMethodId;
        this.userId = userID;
        this.type = type;
        this.provider = provider;
        this.accountNumber = accountNumber;
        this.expiryDate = expiryDate;
        this.billingAddress = billingAddress;
        this.isDefault = isDefault;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
