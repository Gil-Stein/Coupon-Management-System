package couponManagementSystem.facades;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponManagementSystem.beans.CategoryType;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.db.CompanyRepository;
import couponManagementSystem.db.CouponRepository;
import couponManagementSystem.db.CustomerRepository;
import couponManagementSystem.exceptions.CouponExpiredException;
import couponManagementSystem.exceptions.DoubleCouponPurchseException;
import couponManagementSystem.exceptions.InsufficientAmountException;
import couponManagementSystem.exceptions.InvalidLoginExcepction;

@Service
@Scope("prototype")
public class CustomerFacade extends ClientFacade {

	//CTOR
	public CustomerFacade(CompanyRepository compRepo, CustomerRepository custRepo, CouponRepository coupRepo) {
		super(compRepo, custRepo, coupRepo);
	}

	/**
	 * Variable to contain a unique logged in customer id.
	 */
	private int loggedInCustomerId;

	/**
	 * The customer login method will compare given email and password with
	 * information on the database. If credentials exist the method will return a
	 * customer facade and update loggedInCustomerId. Throw custom invalid login
	 * exception if credentials don't exist.
	 */
	@Override
	public boolean login(String email, String password) throws InvalidLoginExcepction {
		if (custRepo.findCustomerByEmail(email).getPassword().equals(password)) {
			loggedInCustomerId = custRepo.findCustomerByEmail(email).getId();
			System.out.println("Logged in with customer id: " + loggedInCustomerId + "...");
			return true;
		}
		throw new InvalidLoginExcepction();
	}

	/**
	 * The purchaseCoupon method will update an entry on the connection table
	 * matching coupons and customers. Once a customer who is logged in and using
	 * the facade asks to purchase a coupon the method will verify it exists in the
	 * database, if it exists, the method will check if it has expired (if so it
	 * will throw a custom exception), next it will check if the logged in customer
	 * already bought the coupon before (if so it ill throw a custom exception),
	 * next it will check if there is a sufficient amount (more than 0) of coupons
	 * in the database. if amount is insufficient the method will throw a custom
	 * exception. If no exception has been thrown the method will add the coupon to
	 * the customer's collection, update the database and reduce the coupon amount
	 * by 1.
	 */
	public void purchaseCoupon(Coupon coupon) throws InsufficientAmountException, 
			CouponExpiredException, DoubleCouponPurchseException {
		
		Coupon couponToPurchase = coupRepo.findById(coupon.getId()).orElseThrow();
		
		if (coupon.getEndDate().before(Calendar.getInstance().getTime()))
			throw new CouponExpiredException();
		
		if (couponToPurchase.getAmount() > 0) {
			couponToPurchase.setAmount(coupon.getAmount() - 1);
			coupRepo.save(coupon);
		} else
			throw new InsufficientAmountException();
		
		Customer cust = custRepo.findById(loggedInCustomerId).orElseThrow();
		Set<Coupon> customerCoupons = getAllCustomerCoupons();
		if (!customerCoupons.contains(couponToPurchase)) {
			System.out.println("Purchasing coupon id: " + couponToPurchase.getId() + ", by customer id: " + loggedInCustomerId);
			customerCoupons.add(couponToPurchase);
			cust.setCoupons(customerCoupons);
			custRepo.save(cust);
		} else
			throw new DoubleCouponPurchseException();
	}

	/**
	 * The getAllCustomerCoupons method returns a List of all the coupons of the
	 * customer which is logged in and using the facade.
	 */
	public Set<Coupon> getAllCustomerCoupons() {
		System.out.println("Getting all logged in customer coupons...");
		return getLoggedInCustomer().getCoupons();
	}

	/**
	 * The getOneCustomerCoupon method returns one Coupon from the collection of
	 * coupons of the logged in customer using the facade.
	 */

	public Coupon getOneCustomerCoupon(int id) {
		System.out.println("Getting logged in customer coupon id: " + id + "...");
		for (Coupon coupon : getAllCustomerCoupons()) {
			if (coupon.getId() == id)
				return coupon;
		}
		throw new NoSuchElementException();
	}

	/**
	 * The getAllCustomerCouponsByCategory method returns a List of the logged in
	 * customer coupons by a given category.
	 */
	public Set<Coupon> getAllCustomerCouponsByCategory(CategoryType category) {
		System.out.println("Getting all logged in customer coupons by category: " + category + "...");
		Set<Coupon> customerCouponsByCategory = new HashSet<Coupon>();
		for (Coupon coupon : getAllCustomerCoupons()) {
			if (coupon.getCategory() == category)
				customerCouponsByCategory.add(coupon);
		}
		return customerCouponsByCategory;
	}

	/**
	 * The getAllCustomerCouponsByMaxPrice method returns a List of the logged in
	 * customer coupons by a given max price.
	 */
	public Set<Coupon> getAllCustomerCouponsByMaxPrice(double maxPrice) {
		System.out.println("Getting all logged in customer coupons by max price: " + maxPrice + "...");
		Set<Coupon> customerCouponsByMaxPrice = new HashSet<Coupon>();
		for (Coupon coupon : getAllCustomerCoupons()) {
			if (coupon.getPrice() <= maxPrice)
				customerCouponsByMaxPrice.add(coupon);
		}
		return customerCouponsByMaxPrice;
	}

	/**
	 * The getLoggedInCustomer method returns the Customer which is logged in and
	 * using the facade.
	 */
	public Customer getLoggedInCustomer() {
		System.out.println("Getting logged in customer...");
		return custRepo.findById(loggedInCustomerId).orElseThrow();
	}

	/**
	 * Get customer by customer id method.
	 * @param customerId
	 * @return Customer with given id.
	 * @throws NoSuchElementException.
	 */
	public Customer getCustomerById(int customerId) {
		System.out.println("Getting customer id: " + customerId + "...");
		return custRepo.findById(customerId).orElseThrow();
	}
	
	/**
	 * Get all coupons method.
	 * @return List of all coupons in the database, or an empty list.
	 */
	public List<Coupon> getAllCoupons() {
		System.out.println("Getting all coupons...");
		return coupRepo.findAll();
	}
	
	/**
	 * Get coupon by coupon id
	 * @param id
	 * @return coupon with given id.
	 * @throws NoSuchElementException.
	 */
	public Coupon getCouponByCouponId(int id) {
		System.out.println("Getting coupon id: " + id + "...");
		return coupRepo.findById(id).orElseThrow();
	}
	
}
