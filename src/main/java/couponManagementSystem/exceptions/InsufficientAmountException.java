package couponManagementSystem.exceptions;

public class InsufficientAmountException extends Exception {

	public InsufficientAmountException() {
		super("Insuffecient amount of coupons, cannot complete purchase...");
	}
}
