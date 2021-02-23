package couponManagementSystem.exceptions;

public class CustomerExistsException extends Exception {

	public CustomerExistsException() {
		super("A Customer with the same email already exists in the database...");
	}
}
