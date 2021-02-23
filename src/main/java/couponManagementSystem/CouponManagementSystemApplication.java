package couponManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;



@SpringBootApplication
public class CouponManagementSystemApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(CouponManagementSystemApplication.class, args);
		
//		Activate the test class to auto generate companies, customers and coupons and test back-end functionality.		
//		Test test= ctx.getBean(Test.class);
//		test.testAll();
		
//		Start the daily coupon clean-up task.
		CouponExpirationDailyJob job = ctx.getBean(CouponExpirationDailyJob.class);
		job.start();
	}

}
