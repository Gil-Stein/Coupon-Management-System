package couponManagementSystem.facades;
import org.springframework.stereotype.Service;

import couponManagementSystem.db.CompanyRepository;
import couponManagementSystem.db.CouponRepository;
import couponManagementSystem.db.CustomerRepository;
import couponManagementSystem.exceptions.InvalidLoginExcepction;


@Service
public abstract class ClientFacade {

	protected CompanyRepository compRepo;
	protected CustomerRepository custRepo;
	protected CouponRepository coupRepo;
	
	public ClientFacade(CompanyRepository compRepo, CustomerRepository custRepo, CouponRepository coupRepo) {
		this.compRepo = compRepo;
		this.custRepo = custRepo;
		this.coupRepo = coupRepo;
	}

	public abstract boolean login(String email, String password) throws InvalidLoginExcepction;

}
