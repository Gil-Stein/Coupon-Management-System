package couponManagementSystem.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import couponManagementSystem.exceptions.InvalidLoginExcepction;
import couponManagementSystem.facades.AdminFacade;
import couponManagementSystem.facades.ClientFacade;
import couponManagementSystem.facades.CompanyFacade;
import couponManagementSystem.facades.CustomerFacade;

@Service
public class LoginManager {
	
	@Autowired
	private ConfigurableApplicationContext ctx; 
	
	public ClientFacade ClientLogin(String email, String password, ClientType client)
			throws InvalidLoginExcepction, NullPointerException {

		switch (client) {

		case Adminisrtator:
			AdminFacade adminFacade = ctx.getBean(AdminFacade.class);
			if (adminFacade.login(email, password));
				return adminFacade;

		case Company:
			CompanyFacade compFacade = ctx.getBean(CompanyFacade.class);
			if (compFacade.login(email, password));
				return compFacade;

		case Customer:
			CustomerFacade custFacade = ctx.getBean(CustomerFacade.class);
			if (custFacade.login(email, password));
				return custFacade;

		default:
			throw new InvalidLoginExcepction();
		}
	}
}
