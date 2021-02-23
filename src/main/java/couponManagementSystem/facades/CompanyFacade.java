package couponManagementSystem.facades;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponManagementSystem.beans.CategoryType;
import couponManagementSystem.beans.Company;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.db.CompanyRepository;
import couponManagementSystem.db.CouponRepository;
import couponManagementSystem.db.CustomerRepository;
import couponManagementSystem.exceptions.CouponExistsException;
import couponManagementSystem.exceptions.CouponUpdateException;
import couponManagementSystem.exceptions.InvalidLoginExcepction;

@Service
@Scope("prototype")
public class CompanyFacade extends ClientFacade {

	//CTOR
	public CompanyFacade(CompanyRepository compRepo, CustomerRepository custRepo, CouponRepository coupRepo) {
		super(compRepo, custRepo, coupRepo);
	}

	/**
	 * Variable to contain the logged in company id.
	 */
	private int loggedInCompanyId;

	/**
	 * The company login method will compare given email and password with information
	 * on the database. If credentials exist the method will return a company facade
	 * and update loggedInCompanyId. Throw custom invalid login exception if
	 * credentials don't exist.
	 */
	@Override
	public boolean login(String email, String password) throws InvalidLoginExcepction, NullPointerException{
		if (compRepo.findCompanyByEmail(email).getPassword().equals(password)) {
			loggedInCompanyId = compRepo.findCompanyByEmail(email).getId();
			System.out.println("Logged in with company id: " + loggedInCompanyId + "...");
			return true;
		}
		throw new InvalidLoginExcepction();
	}

	/**
	 * The addCoupon method will add a new Coupon to the database. Throw custom
	 * exception if the coupon's title and company id already exist in the database.
	 * Return the added coupon including it's auto-generated id.
	 */
	public Coupon addCoupon(Coupon coupon) throws CouponExistsException {
		for (Coupon c : coupRepo.findAll()) {
			if (c.getTitle().equals(coupon.getTitle()) && c.getCompany().equals(coupon.getCompany()))
				throw new CouponExistsException();
		}
		System.out.println("coupon comapny id: " + coupon.getCompany().getId());
		coupon = coupRepo.save(coupon);
		System.out.println("Adding: " + coupon);
		return coupon; 
	}

	/**
	 * The updateCoupon method will update an existing coupon. Throw custom
	 * exception to further constrain customer id updates (the entity's id is
	 * managed by Hibernate and is auto-generated and unique). Return the updated
	 * coupon. 
	 */
	public Coupon updateCoupon(Coupon coupon) throws CouponUpdateException, NoSuchElementException, CouponExistsException{
		Coupon coup = coupRepo.findById(coupon.getId()).orElseThrow();
		if (coup.getCompany().getId() != getLoggedInCompany().getId())
			throw new CouponUpdateException();
		if (coupon.getCompany()==null) 			// JsonIgnore in coupon - allows for admin to see coupons of companies and customers, but doesnt get a coupon with a company so this check is needed
			coupon.setCompany(coup.getCompany());
		for (Coupon c : getAllCompanyCoupons()) {
			if (c.getTitle().equalsIgnoreCase(coupon.getTitle()))
				throw new CouponExistsException();
		}
		coupRepo.save(coupon);
		System.out.println("Updated: " + coupon);
		return coupon;
	}

	/**
	 * The deleteCoupon method will identify the target coupon to be deleted. Throws
	 * NoSuchElement exception if the coupon is not found. Next the method will look
	 * over all customers in the database to check for any target coupon purchases
	 * by customers. If the method find the a target coupon purchase it deletes it
	 * and updates the customer's collection. After going through all customers the
	 * method deleted the coupon by its id.
	 */
	public void deleteCoupon(int id) {
		Coupon coup = coupRepo.findById(id).get();
		if (coup != null) {
			for (Customer customer : custRepo.findAll()) {
				customer.getCoupons().remove(coup);
				System.out.println("Deleting purchase: coupon id " + id + ", by customer id: " + customer.getId());
				custRepo.save(customer);
			}
			System.out.println("Deleted: " + coup);
			coupRepo.deleteById(coup.getId());
		} else
			throw new NoSuchElementException();
	}

	/**
	 * The getAllCompanyCoupons method returns a List of all the coupons of the
	 * company which is logged in and using the facade.
	 */
	public List<Coupon> getAllCompanyCoupons() {
		System.out.println("Getting all logged in company coupons...");
		return getLoggedInCompany().getCoupons();
	}
	

	/**
	 * The getOneCompanyCoupon method returns one Coupon from the collection of
	 * coupons of the logged in company using the facade.
	 */
	public Coupon getOneCompanyCoupon(int id) {
		System.out.println("Getting logged in company coupon id: " + id + "...");
		for (Coupon coupon : getAllCompanyCoupons()) {
			if (coupon.getId() == id)
				return coupon;
		}
		throw new NoSuchElementException();
	}

	/**
	 * Get all company coupons by category method.
	 * @param category
	 * @return List of the logged in company coupons by a given category
	 */
	public List<Coupon> getAllCompanyCouponsByCategory(CategoryType category) {
		System.out.println("Getting all logged in company coupons by category: " + category + "...");
		List<Coupon> companyCouponsByCategory = new ArrayList<Coupon>();
		for (Coupon coupon : getAllCompanyCoupons()) {
			if (coupon.getCategory() == category)
				companyCouponsByCategory.add(coupon);
		}
		return companyCouponsByCategory;
	}

	/**
	 * The getAllCompanyCouponsByMaxPrice method returns a List of the logged in
	 * company coupons by a given max price.
	 */
	public List<Coupon> getAllCompanyCouponsByMaxPrice(double maxPrice) {
		System.out.println("Getting all logged in company coupons by max price: " + maxPrice + "...");
		List<Coupon> companyCouponsByMaxPrice = new ArrayList<Coupon>();
		for (Coupon coupon : getAllCompanyCoupons()) {
			if (coupon.getPrice() <= maxPrice)
				companyCouponsByMaxPrice.add(coupon);
		}
		return companyCouponsByMaxPrice;
	}

	/**
	 * The getLoggedInCompany method returns the Company which is logged in and
	 * using the facade.
	 */
	public Company getLoggedInCompany() {
		System.out.println("Getting logged in company...");
		return compRepo.findById(loggedInCompanyId).orElseThrow();
	}
	
	/**
	 * Get company by id method.
	 * @param companyId
	 * @return Company with given id.
	 * @throws NoSuchElementException.
	 */
	public Company getCompanyById(int companyId) {
		System.out.println("Getting company id: " + companyId + "...");
		return compRepo.findById(companyId).orElseThrow();
	}
	
	/**
	 * Get all company coupons by company id.
	 * @param companyId
	 * @return List of all company coupons by company id, or an empty list.
	 */
	public List<Coupon> getAllCompanyCouponsByCompanyId(int companyId) {
		System.out.println("Getting all coupons of company id: " + companyId + "...");
		return compRepo.getOne(companyId).getCoupons();
	}
}
