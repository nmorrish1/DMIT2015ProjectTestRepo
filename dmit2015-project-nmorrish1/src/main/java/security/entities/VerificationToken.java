package security.entities;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.time.DateUtils;

import lombok.Data;

@Data
@Entity
public class VerificationToken implements Serializable{
	private static final long serialVersionUID = 1L;

	//expiry time in minutes. 1440 for 24 hours
	private static final int EXPIRATION = 3;
	
	@Id
	@GeneratedValue
	private Long token_id;
	
	private String token = generateToken();
	
	private Date expiryDate = setExpiryDate();
	
//	@OneToOne
//	@NotNull
//	@JoinColumn(name="user_id")
//	private User user;
	
	private Long userId;
	
	private Date setExpiryDate() {
		Date currentDate = new Date(System.currentTimeMillis());
		
		return DateUtils.addMinutes(currentDate, EXPIRATION);
	}
	
	private String generateToken() {
		
	    int leftLimit = 47;
	    int rightLimit = 122; // z
	    int targetStringLength = (int) ((Math.random() * ((130 - 120) + 1)) + 100);
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        if(		randomLimitedInt != 94 
	        		&& randomLimitedInt != 96 
	        		&& randomLimitedInt != 58
	        		&& randomLimitedInt != 59
	        		&& randomLimitedInt != 60 
	        		&& randomLimitedInt != 61 
	        		&& randomLimitedInt != 62 
	        		&& randomLimitedInt != 63) {
	        	buffer.append((char) randomLimitedInt);
	        }
	        
	    }
	    return buffer.toString();
	}

}
