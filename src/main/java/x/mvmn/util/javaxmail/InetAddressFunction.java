package x.mvmn.util.javaxmail;

import java.util.function.Function;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class InetAddressFunction implements Function<String, InternetAddress> {

	@Override
	public InternetAddress apply(String t) {
		try {
			return new InternetAddress(t);
		} catch (AddressException e) {
			throw new RuntimeException(e);
		}
	}
}
