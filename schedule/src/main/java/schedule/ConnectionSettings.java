package schedule;

import java.net.InetAddress;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(name="connection")
public class ConnectionSettings {

    private String username;

    private InetAddress remoteAddress;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	@Override
	public String toString() {
		return "ConnectionSettings [username=" + username + ", remoteAddress="
				+ remoteAddress + "]";
	}

    // ... getters and setters

}