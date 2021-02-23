package couponManagementSystem.db;

import org.springframework.data.jpa.repository.JpaRepository;

import couponManagementSystem.beans.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

}
