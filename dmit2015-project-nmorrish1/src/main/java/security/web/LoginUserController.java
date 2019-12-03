package security.web;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

@Named
@ViewScoped
public class LoginUserController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject 
	private Logger logger;
	
	//@EJB
	

}
