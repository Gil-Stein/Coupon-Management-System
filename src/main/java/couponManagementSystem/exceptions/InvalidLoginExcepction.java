package couponManagementSystem.exceptions;

public class InvalidLoginExcepction extends Exception {

	public InvalidLoginExcepction() {
		super("Login failed, invalid username/password...");
	}
}
