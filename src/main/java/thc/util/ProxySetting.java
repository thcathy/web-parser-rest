package thc.util;

public class ProxySetting {
	public final String host;
	public final String port;
	public final String username;
	public final String password;
	
	public ProxySetting(String host, String port, String username, String password) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	public boolean hasProxyUser() {
		return org.apache.commons.lang3.StringUtils.isNotBlank(username) && org.apache.commons.lang3.StringUtils.isNotBlank(password);
	}
	
	public boolean hasProxyServer() {
		return org.apache.commons.lang3.StringUtils.isNotBlank(host) && org.apache.commons.lang3.math.NumberUtils.isDigits(port);
	}
}
