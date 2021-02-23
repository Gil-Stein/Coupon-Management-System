package couponManagementSystem.db;

import org.springframework.data.jpa.repository.JpaRepository;

import couponManagementSystem.beans.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
	Company findCompanyByEmail(String email);
	Company findCompanyByName(String name);
}
