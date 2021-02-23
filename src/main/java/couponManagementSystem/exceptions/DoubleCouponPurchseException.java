package couponManagementSystem.exceptions;

public class DoubleCouponPurchseException extends Exception {
	
	public DoubleCouponPurchseException() {
		super("Cannot purchase coupon, the coupon was already puchased once");
	}

}
