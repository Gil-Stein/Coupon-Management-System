package couponManagementSystem;

/**
 * The Test class runs a test of all actions possible by the different client facades, according to the specifications given.
 */

import java.sql.Date;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import couponManagementSystem.beans.CategoryType;
import couponManagementSystem.beans.Company;
import couponManagementSystem.beans.Coupon;
import couponManagementSystem.beans.Customer;
import couponManagementSystem.facades.AdminFacade;
import couponManagementSystem.facades.CompanyFacade;
import couponManagementSystem.facades.CustomerFacade;
import couponManagementSystem.login.ClientType;
import couponManagementSystem.login.LoginManager;

@Component
public class Test {

	@Autowired
	ConfigurableApplicationContext ctx;
	@Autowired
	AdminFacade admin;
	@Autowired
	CompanyFacade cmpFacade;
	@Autowired
	CustomerFacade cstFacade;
	
	public void testAll() {
		
	// Instantiate Scanner and Random objects to use in Test class
		
		Random rand = new Random();
		Scanner scan = new Scanner(System.in);
		CouponExpirationDailyJob job = ctx.getBean(CouponExpirationDailyJob.class);
		LoginManager manager = ctx.getBean(LoginManager.class);

	// Using a general try/catch clause for all test functions (Nir's instructions)
		try { 
			
			/** 
			 * Start coupon clean up task
			 */
				job.start();

//> ASMINISTRATOR FACADE FUNCTIONS

			System.out.println();
			System.out.println("**************************************");
			System.out.println("Starting administrator facade test....");
			System.out.println("**************************************");
			System.out.println();
			
		/**
		 *  Login - test Administrator login credentials and receive active AdminFacade.
		 */
			AdminFacade admin = (AdminFacade) manager.ClientLogin("admin@admin.com", "admin", ClientType.Adminisrtator);

		/**
		 * Add company - test function to add 2 new companies as admin 
		 */
			Company company = new Company();
			System.out.println("==> Adding companies...");
			for (int i = 0; i < 10; i++) {
				company = new Company("cmp_" + rand.nextInt(100000), "cmp@email_" + rand.nextInt(100000),
						"cmp_password_" + rand.nextInt(100000));
				company = admin.addCompany(company);
			}

		/**
		 *  Update company - test update company's email and password randomly 
		 *  (name cannot be changed)
		 */
			System.out.println("==> Updating company...");
			company = admin.getAllCompanies().get(rand.nextInt(admin.getAllCompanies().size()));
			company.setEmail("cmp@email_updated_" + rand.nextInt(100000));
			company.setPassword("cmp_password_updated_" + rand.nextInt(100000));
			company = admin.updateCompany(company);

		/**
		 *  Add customer - test function to add 2 new customers as Administrator
		 */
			Customer customer = new Customer();
			System.out.println("==> Adding customers...");
			for (int i = 0; i < 10; i++) {
				customer = new Customer("firstName_" + rand.nextInt(100000), "lastName_" 
									+ rand.nextInt(100000),"email_" + rand.nextInt(100000),
									"password_" + rand.nextInt(100000));
				customer = admin.addCustomer(customer);
			}

		/**
		 *  Update customer - test update customer's first and last name, email and password randomly
		 */
			System.out.println("==> Updating customer...");
			customer = admin.getAllCustomers().get(rand.nextInt(admin.getAllCustomers().size()));
			customer.setFirstName("firstName_updated_" + rand.nextInt(100000));
			customer.setLastName("lastName_updated_" + rand.nextInt(1000000));
			customer.setEmail("email_updated_" + rand.nextInt(1000000));
			customer.setPassword("password_updated_" + rand.nextInt(1000000));
			admin.updateCustomer(customer);

		/**
		 *  Print all companies
		 */
			System.out.println("==> Printing all companies in database:");
			for (Company cmp : admin.getAllCompanies()) {
				System.out.println(cmp);
			}

		/**
		 *  Print one company (random)
		 */
			System.out.println("==> Printing one company:");
			System.out.println(admin.getAllCompanies().get(rand.nextInt(admin.getAllCompanies().size())));

		/**
		 *  Print all customers
		 */
			System.out.println("==> Printing all customers in database:");
			for (Customer cstm : admin.getAllCustomers()) {
				System.out.println(cstm);
			}

		/**
		 *  Print one customer (random)
		 */
			System.out.println("==> Printing one customer:");
			System.out.println(admin.getAllCustomers().get(rand.nextInt(admin.getAllCustomers().size())));

//> COMPANY FACADE FUNCTIONS
		
			System.out.println();
			System.out.println("**************************************");
			System.out.println("Starting company facade test....");
			System.out.println("**************************************");
			System.out.println();
			
		
		/**
		 *  Company login and add coupon function - The test function will try to 
		 *  login with each company and add 3 coupons.
		 */
			for (Company comp : admin.getAllCompanies()) {
				cmpFacade = (CompanyFacade) manager.ClientLogin(comp.getEmail(), comp.getPassword(), ClientType.Company);
				System.out.println("==> Adding coupons...");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+rand.nextInt(5));
				for (int i = 0; i < 4; i++) {
					cal.set(Calendar.YEAR, (2020+i));
					Coupon coupon = new Coupon(comp, CategoryType.values()[rand.nextInt(CategoryType.values().length)], 
							"title_" + rand.nextInt(1000000), new Date(Calendar.getInstance().getTimeInMillis()),
							new Date(cal.getTimeInMillis()), rand.nextInt(1000000), "descrip_"+ rand.nextInt(1000000), rand.nextDouble() * 2000, 
							"img_"+ rand.nextInt(1000000));
					cmpFacade.addCoupon(coupon);
				}
			}

		/**
		 *  Update coupon - test update coupon's title, date, set amount and price randomly
		 */
			System.out.println("==> Updating coupon...");
			Coupon coupon = cmpFacade.getAllCompanyCoupons().get(rand.nextInt(cmpFacade.getAllCompanyCoupons().size()));
			coupon.setTitle("title_updated_" + rand.nextInt(1000000));
			coupon.setAmount(rand.nextInt(1000000));
			coupon.setPrice(rand.nextInt(4000));
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, (2555));
			coupon.setEndDate(new Date(cal.getTimeInMillis()));
			cmpFacade.updateCoupon(coupon);

		/**
		 *  Print logged in company details
		 */
			System.out.println("==> Logged in as: " + cmpFacade.getLoggedInCompany());

		/**
		 *  Print all logged in company coupons
		 */
			System.out.println("==> Printing all logged in company coupons in database:");
			for (Coupon coup : cmpFacade.getAllCompanyCoupons()) {
				System.out.println(coup);
			}

		/**
		 *  Print one logged in company coupon (randomly)
		 */
			System.out.println("==> Printing one logged in company coupon:");
			System.out.println(cmpFacade.getAllCompanyCoupons().get(rand.nextInt(cmpFacade.getAllCompanyCoupons().size())));


//> CUSTOMER FACADE FUNCTIONS

			System.out.println();
			System.out.println("**************************************");
			System.out.println("Starting customer facade test....");
			System.out.println("**************************************");
			System.out.println();
	
		/**
		 *  Customer login and purchase coupons - the test function will login with each customer 
		 *  and purchase one of each of the coupons available on the database
		 */
			for (Customer cst : admin.getAllCustomers()) {
				cstFacade = (CustomerFacade) manager.ClientLogin(cst.getEmail(), cst.getPassword(),
						ClientType.Customer);
				for (Coupon coup : admin.getAllCoupons()) {
					/** 
					 * Specific try/catch clause to enable test method to run without stopping
					 * and test also the cases of custom coupon exceptions. 
					 */
					try {
						cstFacade.purchaseCoupon(coup);
					} catch (Exception e) {
						System.out.println(e.getMessage() + ": coupon id " + coup.getId());
					}
				}
			}

		/**
		 *  Print logged in customer details
		 */
			System.out.println("==> Logged in as: " + cstFacade.getLoggedInCustomer());
			
		/**
		 *  Print all logged in customer coupons
		 */
			System.out.println("printing all logged in customer coupons: ");
			for (Coupon coup : cstFacade.getAllCustomerCoupons()) {
				System.out.println(coup);
			}

		/**
		 *  Print all logged in customer coupons by category (random category)
		 */
			CategoryType category = CategoryType.values()[rand.nextInt(CategoryType.values().length)];
			System.out.println("==> Printing all logged in customer coupons by category: " + category);
			for (Coupon coup : cstFacade.getAllCustomerCouponsByCategory(category)) {
				System.out.println(coup);
			}
			
		/**
		 *  Print all logged in customer coupons by max price
		 */
			System.out.println("==> Printing all logged in customer coupons by max price: 2500");
			for (Coupon coup : cstFacade.getAllCustomerCouponsByMaxPrice(2500)) {
				System.out.println(coup);
			}

			
		/**
		 * Pause to confirm database operation and updates before testing delete functions.
		 */
			System.out.println("\nType any key and press enter to continue (test delete)...");
			scan.next();

		/**
		 *  Delete random coupon (of logged in company coupon)
		 */
			System.out.println("==> Deleting coupon...");
			cmpFacade.deleteCoupon(cmpFacade.getAllCompanyCoupons().get(rand.nextInt(cmpFacade.getAllCompanyCoupons().size())).getId());

		/**
		 *  Delete random customer
		 */
			int customerIdToDelete = admin.getAllCustomers().get(rand.nextInt(admin.getAllCustomers().size())).getId();
			System.out.println("==> Deleting customer " + customerIdToDelete + "...");
			admin.deleteCustomer(customerIdToDelete);

		/**
		 * Delete random company
		 */
			int companyIdToDelete = admin.getAllCompanies().get(rand.nextInt(admin.getAllCompanies().size())).getId();
			System.out.println("==> Deleting company " + companyIdToDelete + "...");
			admin.deleteCompany(companyIdToDelete);

		/**
		 *  Stopping coupon clean up task
		 */
			job.stopRunning();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		scan.close();
	}
}
