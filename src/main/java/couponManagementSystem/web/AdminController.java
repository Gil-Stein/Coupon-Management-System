package couponManagementSystem.web;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import couponManagementSystem.beans.Company;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.exceptions.CompanyExistsException;
import couponManagementSystem.exceptions.CompanyUpdateException;
import couponManagementSystem.exceptions.CustomerExistsException;
import couponManagementSystem.facades.AdminFacade;

/**
 * The AdminController class is RestController that handles HTTP requests. All token authentications of sessions are
 * done via the WebConfig class using an AOP approach. Every request with a valid login resets the last login time of the session.
 * Exceptions are handled by the RestController, with a relevant status and body in the response sent to the request sender. 
 *
 */

@RestController
public class AdminController extends ClientController{

	/**
	 * Get all companies method.
	 * @param token
	 * @return List of all companies in the database or empty list.
	 */
	@GetMapping("/companies/{token}")
	public ResponseEntity<List<Company>> getAllCompanies(@PathVariable String token) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		AdminFacade admin = (AdminFacade)session.getFacade();
		List<Company> companies = admin.getAllCompanies();
		return ResponseEntity.ok(companies);
	}
	
	/**
	 * Get all customers method.
	 * @param token
	 * @return List of all customers in the database or empty list.
	 */
	@GetMapping("/customers/{token}")
	public ResponseEntity<?> getAllCustomers(@PathVariable String token) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		return ResponseEntity.ok(((AdminFacade)session.getFacade()).getAllCustomers());
	}
	
	/**
	 * Get one company method.
	 * @param token
	 * @param id
	 * @return Company with the given id.
	 * @throws NoSuchElementExcpetion.
	 */
	@GetMapping ("/companies/{id}/{token}")
	public ResponseEntity<?> getOneCompany(@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			return ResponseEntity.ok(((AdminFacade)session.getFacade()).getOneCompany(id));
		} catch (NoSuchElementException e ) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find a company with id: " + id);
		}
	}
	
	/**
	 * Get one customer method.
	 * @param token
	 * @param id
	 * @return Customer with the given id.
	 * @throws NoSuchElementException. 
	 */
	@GetMapping ("/customers/{id}/{token}")
	public ResponseEntity<?> getOneCustomer(@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			return ResponseEntity.ok(((AdminFacade)session.getFacade()).getOneCustomer(id));
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find customer with id: " + id);
		}
	}

	/**
	 * Get all company coupons by company id method.
	 * @param token
	 * @param id
	 * @return A list of the coupons of the company with the given id, or an empty list.
	 */
	@GetMapping("/companyCoupons/{id}/{token}")
	public ResponseEntity<?> getAllCompanyCouponsByCompanyId(@PathVariable String token, @PathVariable int id) {	
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		List<Coupon> companyCoupons = ((AdminFacade) session.getFacade()).getAllCompanyCouponsByCompanyId(id);
		return ResponseEntity.ok(companyCoupons);
	}
	
	/**
 	 * Get all customer coupons by company id method.
	 * @param token
	 * @param id
	 * @return A list of the coupons of the customer with the given id, or an empty list.

	 */
	@GetMapping("/customerCoupons/{id}/{token}")
	public ResponseEntity<?> getAllCustomerCouponsByCustomerId(@PathVariable String token, @PathVariable int id) {	
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		Set<Coupon> customerCoupons = ((AdminFacade) session.getFacade()).getAllCustomerCouponsByCustomerId(id);
		return ResponseEntity.ok(customerCoupons);
	}
	
	/**
	 * Add company method.
	 * @param token
	 * @param company
	 * @return Status 200 (OK) with String notice.
	 * @throws CompanyExistsException.
	 */
	@PostMapping("/companies/{token}")
	public ResponseEntity<?> addCompany(@PathVariable String token, @RequestBody Company company) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((AdminFacade)session.getFacade()).addCompany(company);
		} catch (CompanyExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok("Company added!");
	}
	
	/**
	 * Add customer method.
	 * @param token
	 * @param customer
	 * @return Status 200 (OK) with String notice.
	 * @throws CustomerExistsException.
	 */
	@PostMapping("/customers/{token}")
	public ResponseEntity<?> addCustomer(@PathVariable String token, @RequestBody Customer customer) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((AdminFacade)session.getFacade()).addCustomer(customer);
		} catch (CustomerExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok("Customer added!");
	}
	
	/**
	 * Delete company by id method.
	 * @param token
	 * @param id
	 * @return Status 200 (OK) with String notice
	 * @throws or NoSuchElementException.
	 */
	@DeleteMapping ("/companies/{id}/{token}")
	public ResponseEntity<?> deleteCompany(@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((AdminFacade)session.getFacade()).deleteCompany(id);
			return ResponseEntity.ok("Company deleted!");
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not find company with given id...");		

		}
	}
	
	/**
	 * Delete customer by id.
	 * @param token
	 * @param id
	 * @return Status 200 (OK) with String notice, 
	 * @throws CustomerExistsException || NoSuchElementException.
	 */
	@DeleteMapping ("/customers/{id}/{token}")
	public ResponseEntity<?> deleteCustomer(@PathVariable String token, @PathVariable int id) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((AdminFacade)session.getFacade()).deleteCustomer(id);
		} catch (CustomerExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find customer with id: " + id);
		}
		return ResponseEntity.ok("Customer deleted!");
	}

	/**
	 * Update company by id method.
	 * @param token
	 * @param company
	 * @return Status 200 (OK) with String notice, or 
	 * @throws CompanyUpdateException || CompanyExistsException || NoSuchElementException.
	 */
	@PutMapping ("/companies/{token}")
	public ResponseEntity<?> updateCompany(@PathVariable String token, @RequestBody Company company) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((AdminFacade)session.getFacade()).updateCompany(company);
		} catch (CompanyUpdateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not find company with given id...");		
		} catch (CompanyExistsException | SQLIntegrityConstraintViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		return ResponseEntity.ok("Company updated!");
	}
	
	/**
	 * Update customer by id method.
	 * @param token
	 * @param customer
	 * @return Status 200 (OK) with String notice
	 * @throws CustomerExistsException || NoSuchElementException.
	 */
	@PutMapping ("/customers/{token}")
	public ResponseEntity<?> updateCustomer(@PathVariable String token, @RequestBody Customer customer) {
		Session session = sessionMap.get(token);
		session.setLastLoginTime(System.currentTimeMillis());
		try {
			((AdminFacade)session.getFacade()).updateCustomer(customer);
		} catch (CustomerExistsException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find customer with id: " + customer.getId());
		}
		return ResponseEntity.ok("Customer updated!");
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
