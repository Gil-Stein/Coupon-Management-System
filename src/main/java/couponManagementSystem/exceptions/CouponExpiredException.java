package couponManagementSystem.exceptions;

public class CouponExpiredException extends Exception {
	
	public CouponExpiredException() {
		super("The coupon has expired, cannot complete purchase...");
	}
}
