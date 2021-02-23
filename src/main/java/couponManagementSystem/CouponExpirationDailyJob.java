package couponManagementSystem;

/**
 * The coupon clean up daily task is a separate thread that runs in one second intervals 
 * over all coupons that exist in the database, checks the date of each one, and if the date 
 * of the coupon has expired, it deletes the coupon and all its purchases by customers from the database. 
 */

import java.util.Calendar;

import org.springframework.stereotype.Service;

import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.db.CouponRepository;
import couponManagementSystem.db.CustomerRepository;

@Service
public class CouponExpirationDailyJob extends Thread {

	private CustomerRepository custRepo;
	private CouponRepository coupRepo;
	private boolean inAction = true;

	
	public CouponExpirationDailyJob(CustomerRepository custRepo, CouponRepository coupRepo) {
		this.custRepo = custRepo;
		this.coupRepo = coupRepo;
	}

	@Override
	public void run() {
		System.out.println("Starting expired coupon clean up task");
		Calendar cal = Calendar.getInstance();
		while (inAction) {
			for (Coupon coupon : coupRepo.findAll()) {
				if (coupon.getEndDate().before(cal.getTime())) {
					for (Customer customer : custRepo.findAll()) {
						for (Coupon customerCoupon : customer.getCoupons()) {
							if (customerCoupon.getId() == coupon.getId()) {
								customer.getCoupons().remove(customerCoupon);
								custRepo.save(customer);
								System.out.println("(Clean up task) ==> Deleted purchase of expired coupon id: "
										+ coupon.getId() + ", by customer id: " + customer.getId());
							}
						}
					}
					coupRepo.deleteById(coupon.getId());
					System.out.println("(Clean up task) ==> Deleted expired coupon id: " + coupon.getId());
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void stopRunning() {
		System.out.println("Stopping job");
		this.inAction = false;
		this.interrupt();
	}
}
