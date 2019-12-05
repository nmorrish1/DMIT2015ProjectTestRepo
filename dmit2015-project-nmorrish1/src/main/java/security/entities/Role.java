package security.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "Role")
@Data
@Entity
@NamedQuery(name="Roles.findAll", query="SELECT r FROM Role r")
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Role implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@NotBlank(message="Enter a role name")
	@Column(length=64, unique=true, nullable=false)
	private String roleName;
	
	@XmlTransient
	@ManyToMany(mappedBy="roles", fetch = FetchType.EAGER)
	private List<User> users;
}
