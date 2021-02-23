package couponManagementSystem.exceptions;

public class CouponUpdateException extends Exception {
	
	public CouponUpdateException() {
		super("Cannot update coupon, company id mistmatch or coupon doesnt exist... ");
	}
}
