package couponManagementSystem.db;

import org.springframework.data.jpa.repository.JpaRepository;

import couponManagementSystem.beans.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	Customer findCustomerByEmail(String email);

}
