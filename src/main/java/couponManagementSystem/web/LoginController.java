package couponManagementSystem.web;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import couponManagementSystem.exceptions.InvalidLoginExcepction;
import couponManagementSystem.facades.ClientFacade;
import couponManagementSystem.login.ClientType;
import couponManagementSystem.login.LoginManager;

/**
 * The LoginController is RestController class to handle all login attempt by users. The controller works with the Login Manager to handle all sessions 
 * and facade allocations to users. When a valid login is given, the login method will generate and return a UUID to be used as an authentication token
 * and saved on the client side to be used when sending requests to the server. Company and customer users will receive their id concatenated to the
 * generated UUID, with a hash sign (#) as a separator to be parsed by the relevant user and stored on the client side.   
 *
 */
@RestController
public class LoginController {

	@Autowired
	private LoginManager manager;
	@Autowired
	private Map<String, Session> sessionMap;

	@PostMapping("/login/{email}/{password}/{clientType}")
	public ResponseEntity<?> login(@PathVariable String email, @PathVariable String password, 
			@PathVariable ClientType clientType) {
		try {
			ClientFacade facade = manager.ClientLogin(email, password, clientType);
			if (facade instanceof ClientFacade) {
				String token = UUID.randomUUID().toString();
				Session session = new Session(facade, System.currentTimeMillis());
				sessionMap.put(token, session);
				return ResponseEntity.ok(token);
			}
		} catch (InvalidLoginExcepction e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		} catch (NullPointerException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed, invalid username/password...");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("General login failure...");
	}
	
	@PostMapping("/logout/{token}")
	public String logout (@PathVariable String token) {
		sessionMap.remove(token);
		System.out.println("logged out");
		return "Logged out...";
	}
		
}
