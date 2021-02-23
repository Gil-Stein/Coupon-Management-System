package couponManagementSystem.exceptions;

public class CompanyExistsException extends Exception {
	
	public CompanyExistsException() {
		super("A company with the same name or email already exists in database...");
	}

}
