package security.entities;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@PasswordMatch
@Data
@Entity
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
@XmlAccessorType(XmlAccessType.FIELD)
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long user_id;
	
	@Size(min=3, max=64, message="Enter a username that contains {min} to {max} characters")
	@Column(length=64, unique=true, nullable=false)
	private String username;
	
	@Column(nullable=false)
	private String password;
	
	@Column(nullable=false)
	@Pattern(regexp="^.+@.+\\.[a-zA-Z]{2,4}$", message = "Please enter a valid email address")
	private String email;
	
	@Column(nullable=false)
	private Boolean verified = false;
	
	@Column(nullable=false)
	private Boolean locked = false;
	
	
	
	@XmlTransient
	@Transient
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
		message="Password value must contain at least 8 characters with at least 1 uppercase letter, 1 lowercase letter, and 1 number")
	private String plainTextPassword;
	
	@XmlTransient
	@Transient
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
		message="Confirm Password value must contain at least 8 characters with at least 1 uppercase letter, 1 lowercase letter, and 1 number")
	private String confirmedPlainTextPassword;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="UserRole", 
		joinColumns={
			@JoinColumn(name="userid")
		}, 
		inverseJoinColumns={
			@JoinColumn(name="roleid")
		}
	)
	private List<Role> roles = new ArrayList<>();

}
