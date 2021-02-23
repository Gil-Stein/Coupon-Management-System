package couponManagementSystem.web;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import couponManagementSystem.beans.CategoryType;
import couponManagementSystem.beans.Company;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.exceptions.CouponExistsException;
import couponManagementSystem.exceptions.CouponUpdateException;
import couponManagementSystem.facades.CompanyFacade;

/**
 * The CompanyController class is RestController that handles HTTP requests. All token authentications of sessions are
 * done via the WebConfig class using an AOP approach. Every request with a valid login resets the last login time of the session.
 * Exceptions are handled by the RestController, with a relevant status and body in the response sent to the request sender. 
 *
 */

@RestController
@RequestMapping("/company")
public class CompanyController extends ClientController{

	/**
	 * Add coupon method.
	 * @param token
	 * @param coupon
	 * @return Http status 200 (OK) with String notice.
	 * @handles CouponExistsException.
	 */
	@PostMapping("/{token}")
	public ResponseEntity<?> addCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
		Session session = sessionMap.get(token);
		CompanyFacade facade = (CompanyFacade) session.getFacade();
		coupon.setCompany(facade.getLoggedInCompany());
		try {
			facade.addCoupon(coupon);
			session.setLastLoginTime(System.currentTimeMillis());
			return ResponseEntity.ok("Coupon added!");
		} catch (CouponExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	/**
	 * Get all company coupons.
	 * @param token
	 * @return List of all logged in company coupons, or an empty list. 
	 */
	@GetMapping("/{token}")
	public ResponseEntity<?> getAllCompanyCoupons(@PathVariable String token) {	
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		List<Coupon> companyCoupons = ((CompanyFacade) session.getFacade()).getAllCompanyCoupons();
		return ResponseEntity.ok(companyCoupons);
	}
	
	/**
	 * Get all company coupons by category.
	 * @param token
	 * @param category
	 * @return List of all company coupons by category, or an empty list. 
	 */
	@GetMapping("/companyCouponsByCategory/{category}/{token}")
	public ResponseEntity<?> getAllCompanyCouponsByCategory(@PathVariable String token, 
			@PathVariable CategoryType category) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		List<Coupon> companyCouponsByCategory;
		if (category != null) {
			companyCouponsByCategory = ((CompanyFacade) session.getFacade()).getAllCompanyCouponsByCategory(category);
		} else {
			companyCouponsByCategory = ((CompanyFacade) session.getFacade()).getAllCompanyCoupons();
		}
		return ResponseEntity.ok(companyCouponsByCategory);
	}
	
	/**
	 * Get all company coupons by max price.
	 * @param token
	 * @param maxPrice
	 * @return List of all company coupons by max price, or an empty list. 
	 */
	@GetMapping("/companyCouponsByMaxPrice/{maxPrice}/{token}")
	public ResponseEntity<?> getAllCompanyCouponsByMaxPrice(@PathVariable String token, 
			@PathVariable double maxPrice) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		List<Coupon> companyCouponsByMaxPrice;
		companyCouponsByMaxPrice= ((CompanyFacade) session.getFacade()).getAllCompanyCouponsByMaxPrice(maxPrice);
		return ResponseEntity.ok(companyCouponsByMaxPrice);
	}
	
	/**
	 * Get one company coupon by coupon id. 
	 * @param token
	 * @param id
	 * @return Coupon with give id.
	 * @handles NoSuchElementException.
	 */
	@GetMapping("/coupons/{token}/{id}")
	public ResponseEntity<?> getOneCoupon(@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			return ResponseEntity.ok(((CompanyFacade) session.getFacade()).getOneCompanyCoupon(id));
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find a coupon with id: " + id);
		}
	}
	
	/**
	 * Update coupon method.
	 * @param token
	 * @param coupon
	 * @return Http status 200 (OK) with String notice.
	 * @handles CouponUpdateException | CouponExistsException | NoSuchElementException
	 */
	@PutMapping("/coupons/{token}")
	public ResponseEntity<?> updateCoupon(@PathVariable String token, @RequestBody Coupon coupon) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((CompanyFacade) session.getFacade()).updateCoupon(coupon);
		} catch (CouponUpdateException | CouponExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find a coupon with id: " + coupon.getId());
		}
		return ResponseEntity.ok("Coupon updated!");
	}

	/**
	 * Delete coupon by id method.
	 * @param token
	 * @param id
	 * @return Http status 200 (OK) with String notice.
	 * @handles NoSuchElementException.
	 * 
	 */
	@DeleteMapping("/coupons/{token}/{id}")
	public ResponseEntity<?> deleteCoupon (@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((CompanyFacade) session.getFacade()).deleteCoupon(id);
			return ResponseEntity.ok("Coupon deleted!");			
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find a coupon with id: " + id);
		}
	}
		
	/**
	 * Get all company coupons by company id.
	 * @param token
	 * @param id
	 * @return List of company coupons by company id, or an empty list. 
	 */
	@GetMapping("/getCompanyCouponsByCompanyId/{id}/{token}")
	public ResponseEntity<?> getCompanyCouponsByCompanyId(@PathVariable String token, @PathVariable int id) {	
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		List<Coupon> companyCoupons = null;
			 companyCoupons = ((CompanyFacade) session.getFacade()).getAllCompanyCouponsByCompanyId(id);
		return ResponseEntity.ok(companyCoupons);
	}

	
	@GetMapping("/getLoggedInCompany/{token}")
	public ResponseEntity<?> getLoggedInCompany(@PathVariable String token) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			Company company =  ((CompanyFacade) session.getFacade()).getLoggedInCompany();
			return ResponseEntity.ok(company);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find logged in company...");
		}
	}
	
	/**
	 * To comply with specification - ClientController is an abstract class with a boolean login method.
	 * Method is not used in the system implementation, login is managed by the LoginController. 
	 */
	@Override
	public boolean login(String email, String password) {
		return false;
	}
}
