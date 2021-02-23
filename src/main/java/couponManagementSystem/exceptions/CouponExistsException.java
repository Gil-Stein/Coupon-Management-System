package couponManagementSystem.exceptions;

public class CouponExistsException extends Exception {
	
	public CouponExistsException() {
		super("Cannot add/update coupon, a coupon with the same title and company id already exists in the database...");
	}
}
