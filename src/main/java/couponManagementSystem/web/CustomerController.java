package couponManagementSystem.web;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import couponManagementSystem.beans.CategoryType;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.exceptions.CouponExpiredException;
import couponManagementSystem.exceptions.DoubleCouponPurchseException;
import couponManagementSystem.exceptions.InsufficientAmountException;
import couponManagementSystem.facades.CustomerFacade;

/**
 * The CustomerController class is RestController that handles HTTP requests. All token authentications of sessions are
 * done via the WebConfig class using an AOP approach. Every request with a valid login resets the last login time of the session.
 * Exceptions are handled by the RestController, with a relevant status and body in the response sent to the request sender. 
 *
 */

@RestController
@RequestMapping("/customer")
public class CustomerController extends ClientController{

	/**
	 * Purchase coupon by coupon id method.
	 * @param token
	 * @param couponId
	 * @return Http status 200 (OK) with String notice.
	 * @handles InsufficientAmountException | CouponExpiredException | DoubleCouponPurchseException
	 */
	@GetMapping("/purchase/{couponId}/{token}")
	public ResponseEntity<?> purchaseCouponByCouponId(@PathVariable String token, @PathVariable int couponId) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		Coupon coupon = ((CustomerFacade) session.getFacade()).getCouponByCouponId(couponId);
		try {
			((CustomerFacade) session.getFacade()).purchaseCoupon(coupon);
			return ResponseEntity.ok("Coupon purchased");
		} catch (InsufficientAmountException | CouponExpiredException | DoubleCouponPurchseException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Get all customer coupons method.
	 * @param token
	 * @return A list of of all customer coupons, or an empty list.
	 */
	@GetMapping("/coupons/{token}")
	public ResponseEntity<?> getAllCustomerCoupons(@PathVariable String token) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		return ResponseEntity.ok(((CustomerFacade) session.getFacade()).getAllCustomerCoupons());
	}

	/**
	 * Get all customer coupons by category method.
	 * @param token, category.
	 * @return A list of of all customer coupons by category, or an empty list.
	 */
	@GetMapping("/couponsByCategory/{category}/{token}")
	public ResponseEntity<?> getAllCustomerCouponsByCategory(@PathVariable String token,	@PathVariable CategoryType category) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		Set<Coupon> customerCouponsByCategory;
		customerCouponsByCategory = ((CustomerFacade) session.getFacade()).getAllCustomerCouponsByCategory(category);
		return ResponseEntity.ok(customerCouponsByCategory);
	}

	/**
	 * Get all customer coupons by max price method.
	 * @param token, maxPrice
	 * @return A list of of all customer coupons by max price, or an empty list.
	 */
	@GetMapping("/couponsByPrice/{maxPrice}/{token}")
	public ResponseEntity<?> getAllCustomerCouponsByMaxPrice(@PathVariable String token, @PathVariable double maxPrice) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		Set<Coupon> customerCouponsByMaxPrice;
		customerCouponsByMaxPrice = ((CustomerFacade) session.getFacade()).getAllCustomerCouponsByMaxPrice(maxPrice);
		return ResponseEntity.ok(customerCouponsByMaxPrice);
	}

	/**
	 * Get one customer coupon by coupon id method.
	 * @param token
	 * @param id
	 * @return Coupon with given id.
	 * @handles NoSuchElementException
	 */
	@GetMapping("/couponsById/{id}/{token}")
	public ResponseEntity<?> getOneCustomerCoupon(@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			return ResponseEntity.ok(((CustomerFacade) session.getFacade()).getOneCustomerCoupon(id));
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find coupon with id: " + id);
		}
	}

	/**
	 * Get all coupons in the database method.
	 * @param token
	 * @return List of all coupons in the database, or an empty list.
	 */
	@GetMapping("/allCoupons/{token}")
	public ResponseEntity<?> getAllCoupons(@PathVariable String token) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		List<Coupon> allCoupons = ((CustomerFacade) session.getFacade()).getAllCoupons();
		return ResponseEntity.ok(allCoupons);
	}
	
	/**
	 * Get logged in user details
	 * @param token
	 * @return the logged in customer
	 * @handles NoSuchElementException
	 */
	
	@GetMapping("/getLoggedInCustomer/{token}")
	public ResponseEntity<?> getLoggedInCustomer(@PathVariable String token) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			Customer customer =  ((CustomerFacade) session.getFacade()).getLoggedInCustomer();
			return ResponseEntity.ok(customer);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find logged in customer...");
		}
	}
	
	/**
	 * To comply with specification - ClientController is an abstract class with a boolean login method.
	 * Method is not used in the system implementation, login is managed by the LoginController 
	 */
	@Override
	public boolean login(String email, String password) {
		return false;
	}
}
