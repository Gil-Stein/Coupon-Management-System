package couponManagementSystem.web;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class ClientController {

	@Autowired
	protected Map<String, Session> sessionMap;
	
	public abstract boolean login (String email, String password);
}
