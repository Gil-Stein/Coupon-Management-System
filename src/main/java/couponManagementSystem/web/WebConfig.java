package couponManagementSystem.web;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import couponManagementSystem.facades.AdminFacade;
import couponManagementSystem.facades.CompanyFacade;
import couponManagementSystem.facades.CustomerFacade;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This is web configuration class. The class defines two beans, one to hold the
 * singleton HashMap to register all active sessions, the relevant facade and
 * login time. and another bean to enable Swagger 2 User Interface. AOP based
 * methods wrap around all methods executed by the Administrator, Company and Customer
 * controllers. The purpose of these wrapper methods is to authenticate the
 * validity of the login of each request sent by users. The system is designed so
 * that the methods in the controllers have a token variable that in the 0
 * ordinal place. the ProceedingJoinPoint object takes that token and verifies
 * its validity using the session map. Login will time out after 30 minutes of
 * no requests. Invalid tokens will be removed from the session map. Once the
 * validity of the token has been authenticated the JoinPoint continues to the
 * method in the relevant web controller.
 *
 */

@Configuration
@EnableSwagger2
@Aspect
public class WebConfig implements WebMvcConfigurer{
		
	
	/**
	 * The addViewControllers and the containerCustomizer methods are put used to streamline the internal routing
	 * of the Angular based Single Page Application. Users that try to navigate to an unknown route get redirected to index.html.
	 */
	
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/notFound").setViewName("forward:/index.html");
    }

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> containerCustomizer() {
        return container -> {
            container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notFound"));
        };
    }
    
    /**
     * A single session map to hold all tokens and live user facades.
     */
	@Bean
	public Map<String, Session> sessionMap() {
		return new HashMap<String, Session>();
	}
	
	/**
	 * User authentication method for Administrator facade. Will run before all method in the AdminController. 
	 */
	@Around("execution(* couponManagementSystem.web.AdminController.*(..))")
	public ResponseEntity<?> adminAuthenticate(ProceedingJoinPoint point) throws Throwable {
		String token = (String) point.getArgs()[0];
		if (sessionMap().containsKey(token)) {
			Session currentSession = sessionMap().get(token);
			if (currentSession.getFacade() instanceof AdminFacade) {
				if (System.currentTimeMillis() - currentSession.getLastLoginTime() < 1000 * 60 * 30) {
					return (ResponseEntity<?>) point.proceed();
				} else {
					sessionMap().remove(token);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login timeout...");
				}
			} else {
				sessionMap().remove(token);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Unauthorized login, token and facade mismatch...");
			}
		} else {
			sessionMap().remove(token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Unauthorized login, token expired or is invalid...");
		}
	}

	/**
	 * User authentication method for Company facade. Will run before all method in the CompanyController. 
	 */
	@Around("execution(* couponManagementSystem.web.CompanyController.*(..))")
	public ResponseEntity<?> CompanyAuthenticate(ProceedingJoinPoint point) throws Throwable {
		String token = (String) point.getArgs()[0];
		if (sessionMap().containsKey(token)) {
			Session currentSession = sessionMap().get(token);
			if (currentSession.getFacade() instanceof CompanyFacade) {
				if (System.currentTimeMillis() - currentSession.getLastLoginTime() < 1000 * 60 * 30) {
					return (ResponseEntity<?>) point.proceed();
				} else {
					sessionMap().remove(token);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login timeout...");
				}
			} else {
				sessionMap().remove(token);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login, token mismatch...");
			}
		} else {
			sessionMap().remove(token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login, invalid token...");
		}
	}

	/**
	 * User authentication method for Customer facade. Will run before all method in the CustomerController. 
	 */
	@Around("execution(* couponManagementSystem.web.CustomerController.*(..))")
	public ResponseEntity<?> CustomerAuthenticate(ProceedingJoinPoint point) throws Throwable {
		String token = (String) point.getArgs()[0];
		if (sessionMap().containsKey(token)) {
			Session currentSession = sessionMap().get(token);
			if (currentSession.getFacade() instanceof CustomerFacade) {
				if (System.currentTimeMillis() - currentSession.getLastLoginTime() < 1000 * 60 * 30) {
					return (ResponseEntity<?>) point.proceed();
				} else {
					sessionMap().remove(token);
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login timeout...");
				}
			} else {
				sessionMap().remove(token);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login, token mismatch...");
			}
		} else {
			sessionMap().remove(token);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized login, invalid token...");
		}
	}
	
	/**
	 * To enable Swagger 2
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}
}
