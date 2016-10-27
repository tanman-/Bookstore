package au.edu.uts.aip.domain.ejb;

import au.edu.uts.aip.domain.entity.User;
import au.edu.uts.aip.domain.exception.ActivationException;
import au.edu.uts.aip.domain.exception.InvalidTokenException;
import au.edu.uts.aip.domain.validation.ValidationResult;
import javax.ejb.Remote;

/**
 *
 * @author sondang
 */
@Remote
public interface UserRemote {
    User getUser(String username);
    ValidationResult createUser(User user);
    String generateActivationToken(User user);
    void activateAccount(String token, String username) throws ActivationException, InvalidTokenException;
}
