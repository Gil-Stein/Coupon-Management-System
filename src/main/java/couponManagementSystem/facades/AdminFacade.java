package couponManagementSystem.facades;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import couponManagementSystem.beans.Company;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.db.CompanyRepository;
import couponManagementSystem.db.CouponRepository;
import couponManagementSystem.db.CustomerRepository;
import couponManagementSystem.exceptions.CompanyExistsException;
import couponManagementSystem.exceptions.CompanyUpdateException;
import couponManagementSystem.exceptions.CustomerExistsException;
import couponManagementSystem.exceptions.InvalidLoginExcepction;

@Service
@Scope("prototype")
public class AdminFacade extends ClientFacade {

	//CTOR
	public AdminFacade(CompanyRepository compRepo, CustomerRepository custRepo, CouponRepository coupRepo) {
		super(compRepo, custRepo, coupRepo);
	}

	/**
	 * Hard coded Administrator login method, returns custom exception if login failed. 
	 */
	@Override
	public boolean login(String email, String password) throws InvalidLoginExcepction {
		if (email.equals("admin@admin.com") && password.equals("admin")) {
			System.out.println("Logged in Administrator...");
			return true;
		}
		throw new InvalidLoginExcepction();
	}

	/**
	 *  The addCompany method will add a new company to the database. Throw custom exception if the 
	 *  company's email or name already exist in the database. Return the
	 *  added company including it's auto-generated id.
	 */
	public Company addCompany(Company company) throws CompanyExistsException {
		if (compRepo.findCompanyByEmail(company.getEmail()) != null
				|| compRepo.findCompanyByName(company.getName()) != null)
			throw new CompanyExistsException();
		company = compRepo.save(company);
		System.out.println("Added: " + company);
		return company;
	}

	/**
	 *  The addCustomer method will add a new customer to the database. Throw custom exception if the 
	 *  customer's email already exists in the database. Return the added customer 
	 *  including it's auto-generated id.
	 */
	public Customer addCustomer(Customer customer) throws CustomerExistsException {
		if (custRepo.findCustomerByEmail(customer.getEmail()) != null)
			throw new CustomerExistsException();
		customer = custRepo.save(customer);
		System.out.println("Added: " + customer);
		return customer;
	}

	/**
	 * The updateCompany method will update an existing company. Throw custom exception to constrain company name 
	 * updates. Return the updated company.  
	 * @throws CompanyExistsException 
	 */
	
	public Company updateCompany(Company company) throws SQLIntegrityConstraintViolationException, 
	CompanyUpdateException, NoSuchElementException, CompanyExistsException{
		Company c = compRepo.findById(company.getId()).orElseThrow();
		if (!company.getName().equalsIgnoreCase(c.getName()))
			throw new CompanyUpdateException();
		for (Company comp : compRepo.findAll()) {
			if(comp.getEmail().equalsIgnoreCase(company.getEmail()) && comp.getId() != company.getId())
				throw new CompanyExistsException();
		}
		company = compRepo.save(company);
		System.out.println("updated: " + company);
		return company;
	}

	/**
	 * The updateCustomer method will update an existing customer. Throw No Such Element Exception to 
	 * further constrain customer id updates (the entity's id is managed by Hibernate 
	 * and is auto-generated and unique). Return the updated customer.  
	 * @throws CustomerExistsException 
	 */
	
	public Customer updateCustomer(Customer customer) throws NoSuchElementException, CustomerExistsException {
		if (custRepo.existsById(customer.getId())) {
			for (Customer c : custRepo.findAll()) {
				if (c.getEmail().equalsIgnoreCase(customer.getEmail()) && c.getId() != customer.getId())
					throw new CustomerExistsException();
			}
			customer = custRepo.save(customer);
			System.out.println("Updated: " + customer);
			return customer;
		} else throw new NoSuchElementException();
	}

	/**
	 * The deleteCompany method will identify the target company to be deleted. If the company
	 * is not found the method will throw a NoSuchElement Exception. 
	 * Next it will loop over its coupons collection and all customer coupons collections.
	 * If the method identifies that a customer has one of the company coupons in his collection 
	 * (meaning he purchased the coupon), the method will delete the coupon from the customer's
	 * collection. After all the customer purchases are deleted, the method will delete the company
	 * coupon and move on to the next one. After the method deleted all company coupons, it will
	 * delete the company. 
	 */
	
	public void deleteCompany(int id) throws NoSuchElementException{
		Company company = compRepo.findById(id).orElseThrow();
		if (company != null) {
			for (Coupon companyCoupon : compRepo.findById(id).get().getCoupons()) {
				for (Customer customer : custRepo.findAll()) {
					customer.getCoupons().remove(companyCoupon);
					System.out.println("Deleting purchase: coupon id " + companyCoupon.getId() + ", by customer id: "
							+ customer.getId());
					custRepo.save(customer);
				}
				System.out.println("Deleted: " + companyCoupon);
				coupRepo.deleteById(companyCoupon.getId());
			}
			compRepo.deleteById(id);
			System.out.println("Deleted: " + company);
		} else throw new NoSuchElementException();
	}

	/**
	 * The deleteCustomer method will identify the target customer to be deleted. If the customer
	 * is not found the method will throw a NoSuchElement Exception. 
	 * Next the method will delete all coupons from the customer's collection. After all the 
	 * customer purchases are deleted, the method will delete the customer.
	 * @throws CustomerExistsException 
	 */
	
	public void deleteCustomer(int id) throws NoSuchElementException, CustomerExistsException {
		Customer customer = custRepo.findById(id).orElseThrow();
		if (customer != null) {
			System.out.println("==> Deleting customer coupon purchases: " + customer.getCoupons());
			customer.getCoupons().removeAll(customer.getCoupons());
			updateCustomer(customer);
			custRepo.deleteById(id);
			System.out.println("Deleted: " + customer);
		} else throw new NoSuchElementException();
	}

	/**
	 * The getAllCompanies method returns a List object of all companies in the database.
	 */
	
	public List<Company> getAllCompanies() {
		System.out.println("getting all companies...");
		return compRepo.findAll();
	}

	/**
	 * The getOneCompany method returns a Company object from the database according to given id.
	 */
	
	public Company getOneCompany(int id) throws NoSuchElementException {
		System.out.println("Getting company id: " + id + "...");
		return compRepo.findById(id).orElseThrow();
	}
	

	/**
	 * The getAllCustomers method returns a List of all customers in the database.
	 */
	
	public List<Customer> getAllCustomers() {
		System.out.println("Getting all customers...");
		return custRepo.findAll();
	}

	/**
	 * The getOneCustomer method returns a Customer object from the database according to given id.
	 */
	
	public Customer getOneCustomer(int id) {
		System.out.println("Getting customer id: " + id + "...");
		return custRepo.findById(id).orElseThrow();
	}

	/**
	 * The getAllCoupons methods returns a List of all coupons in the database. 
	 * */
	
	public List<Coupon> getAllCoupons() {
		System.out.println("Getting all coupons...");
		return coupRepo.findAll();
	}

	/**
	 * Get all company coupons by company id method
	 * @param companyId
	 * @return List of company coupons, or an empty list.
	 */
	public List<Coupon> getAllCompanyCouponsByCompanyId(int companyId) {
		System.out.println("Getting all coupons of company id: " + companyId + "...");
		return compRepo.getOne(companyId).getCoupons();
	}
	
	/**
	 * Get all customer coupons by customer id method
	 * @param customerId
	 * @return List of customer coupons, or an empty list.
	 */
	public Set<Coupon> getAllCustomerCouponsByCustomerId(int customerId) {
		System.out.println("Getting all coupons of customer id: " + customerId + "...");
		return custRepo.getOne(customerId).getCoupons();
	}
}
