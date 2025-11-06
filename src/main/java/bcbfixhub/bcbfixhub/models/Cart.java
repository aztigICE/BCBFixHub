package bcbfixhub.bcbfixhub.models;
import java.time.LocalDate;

public class Cart {
    private int cartId;
    private int userId;
    private LocalDate createdDate;
    private String status;

    public Cart() {
    }

    public Cart(int cartId, int userId, LocalDate createdDate, String status) {
        this.cartId = cartId;
        this.userId = userId;
        this.createdDate = createdDate;
        this.status = status;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
