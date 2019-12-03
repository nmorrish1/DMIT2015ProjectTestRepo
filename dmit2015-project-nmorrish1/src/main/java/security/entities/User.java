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


//@PasswordMatch
@Data
@Entity
@NamedQuery(name="LoginUser.findAll", query="SELECT u FROM LoginUser u")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

}
