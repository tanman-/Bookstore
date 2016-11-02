package au.edu.uts.aip.domain.remote;

import au.edu.uts.aip.domain.pin.dto.PinCardCreate;
import au.edu.uts.aip.domain.pin.dto.PinCharge;
import au.edu.uts.aip.domain.pin.dto.PinCustomerCreate;
import javax.ejb.Remote;
import javax.ws.rs.core.Response;

@Remote
public interface PaymentRemote {

    Response charge(PinCharge pinCharge);

    Response createCard(PinCardCreate pinCardCreate);

    Response createCustomer(PinCustomerCreate pinCustomerCreate);
    
}