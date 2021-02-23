package couponManagementSystem.web;

import couponManagementSystem.facades.ClientFacade;

/**
 * The session class is used to handle multiple login requests and to allocate multiple facades to users.
 * The last login time variable is used by the system to limit login duration.
 *
 */

public class Session {

	private ClientFacade facade;
	private long lastLoginTime;

	public Session(ClientFacade facade, long lastLoginTime) {
		this.facade = facade;
		this.lastLoginTime = lastLoginTime;
	}

	public ClientFacade getFacade() {
		return facade;
	}

	public void setFacade(ClientFacade facade) {
		this.facade = facade;
	}

	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

}
