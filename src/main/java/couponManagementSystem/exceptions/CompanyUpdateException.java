package couponManagementSystem.exceptions;

public class CompanyUpdateException extends Exception {

	public CompanyUpdateException() {
		super("Company name/id mismatch, company name and id must match to update a company...");
	}
}
