package au.edu.uts.aip.domain.ejb;

import au.edu.uts.aip.domain.remote.PaymentRemote;
import au.edu.uts.aip.domain.pin.dto.PinChargePost;
import au.edu.uts.aip.domain.pin.dto.PinCustomerPost;
import au.edu.uts.aip.domain.pin.dto.PinRecipientPost;
import au.edu.uts.aip.domain.pin.dto.PinRecipientPut;
import au.edu.uts.aip.domain.pin.dto.PinTransferPost;
import au.edu.uts.aip.domain.pin.filter.BasicAuthFilter;
import au.edu.uts.aip.domain.pin.filter.ClientResponseLoggingFilter;
import au.edu.uts.aip.domain.pin.utility.PinResponseUtility;
import au.edu.uts.aip.domain.validation.ValidationResult;
import javax.ejb.Stateless;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
public class PaymentBean implements PaymentRemote {

    private static final String BASE_URL = "https://test-api.pin.net.au/1";
    private static final String API_KEY_SECRET = "***REMOVED***";
    private static final String PASSWORD = "";

    @Override
    public ValidationResult createCustomer(PinCustomerPost pinCustomerPost) {

        Client client = ClientBuilder.newClient()
                .register(new BasicAuthFilter(API_KEY_SECRET, PASSWORD))
                .register(new ClientResponseLoggingFilter());

        Response response = client.target(BASE_URL + "/customers")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pinCustomerPost, MediaType.APPLICATION_JSON_TYPE));

        int statusCode = response.getStatus();
        JsonObject responseJson = PinResponseUtility.toJson(response.readEntity(String.class));
        client.close();

        return PinResponseUtility.validate(statusCode, responseJson);
    }

    @Override
    public ValidationResult charge(PinChargePost pinChargePost) {

        Client client = ClientBuilder.newClient()
                .register(new BasicAuthFilter(API_KEY_SECRET, PASSWORD))
                .register(new ClientResponseLoggingFilter());

        Response response = client.target(BASE_URL + "/charges")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pinChargePost, MediaType.APPLICATION_JSON_TYPE));

        int statusCode = response.getStatus();
        JsonObject responseJson = PinResponseUtility.toJson(response.readEntity(String.class));
        client.close();

        return PinResponseUtility.validate(statusCode, responseJson);
    }

    @Override
    public ValidationResult createRecipient(PinRecipientPost pinRecipientPost) {
        Client client = ClientBuilder.newClient()
                .register(new BasicAuthFilter(API_KEY_SECRET, PASSWORD))
                .register(new ClientResponseLoggingFilter());

        Response response = client.target(BASE_URL + "/recipients")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pinRecipientPost, MediaType.APPLICATION_JSON_TYPE));

        int statusCode = response.getStatus();
        JsonObject responseJson = PinResponseUtility.toJson(response.readEntity(String.class));
        client.close();

        return PinResponseUtility.validate(statusCode, responseJson);
    }

    @Override
    public ValidationResult transfer(PinTransferPost pinTransferPost) {
        Client client = ClientBuilder.newClient()
                .register(new BasicAuthFilter(API_KEY_SECRET, PASSWORD))
                .register(new ClientResponseLoggingFilter());

        Response response = client.target(BASE_URL + "/transfers")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(pinTransferPost, MediaType.APPLICATION_JSON_TYPE));

        int statusCode = response.getStatus();
        JsonObject responseJson = PinResponseUtility.toJson(response.readEntity(String.class));
        client.close();

        return PinResponseUtility.validate(statusCode, responseJson);
    }

    @Override
    public JsonObject fetchRecipient(String recipientToken) {
        Client client = ClientBuilder.newClient()
                .register(new BasicAuthFilter(API_KEY_SECRET, PASSWORD))
                .register(new ClientResponseLoggingFilter());

        Response response = client.target(BASE_URL + "/recipients")
                .path("{recipient-token}")
                .resolveTemplate("recipient-token", recipientToken)
                .request()
                .get();

        JsonObject responseJson = PinResponseUtility.toJson(response.readEntity(String.class));
        client.close();

        return responseJson;
    }

    @Override
    public ValidationResult editRecipient(String recipientToken, PinRecipientPut pinRecipientPut) {
        Client client = ClientBuilder.newClient()
                .register(new BasicAuthFilter(API_KEY_SECRET, PASSWORD))
                .register(new ClientResponseLoggingFilter());

        Response response = client.target(BASE_URL + "/recipients")
                .path("{recipient-token}")
                .resolveTemplate("recipient-token", recipientToken)
                .request()
                .put(Entity.entity(pinRecipientPut, MediaType.APPLICATION_JSON_TYPE));

        int statusCode = response.getStatus();
        JsonObject responseJson = PinResponseUtility.toJson(response.readEntity(String.class));
        client.close();

        return PinResponseUtility.validate(statusCode, responseJson);
    }
}
