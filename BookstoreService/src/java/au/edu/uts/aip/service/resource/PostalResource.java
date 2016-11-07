package au.edu.uts.aip.service.resource;

import au.edu.uts.aip.domain.response.SerialResponse;
import au.edu.uts.aip.domain.remote.PostalDataRemote;
import au.edu.uts.aip.domain.remote.PostalFeeRemote;

import javax.ejb.EJB;
import javax.json.Json;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST endpoint for postal data access and postal fee calculation
 * @author Son Dang, Alex Tan, Xiaoyang Liu
 */
@Path("postal")
public class PostalResource {

    @EJB
    private PostalFeeRemote postalFeeBean;
    @EJB
    private PostalDataRemote postalDataBean;

    /**
     *
     * @param postcodeString
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({"USER", "ADMIN"})
    @Path("state/{postcode}")
    public Response fetchState(@PathParam("postcode") String postcodeString) {
        int postcode;

        try {
            postcode = Integer.parseInt(postcodeString);
        } catch (NumberFormatException ex) {
            return Response.status(422).entity(Json.createObjectBuilder().add("error", "Invalid postcode. Postcode must be numeric.").build()).build();
        }
        if (postcodeString.length() != 4) {
            return Response.status(422).entity(Json.createObjectBuilder().add("error", "Invalid postcode. Postcode must be four digits.").build()).build();
        }
        SerialResponse response = postalDataBean.getStateName(postcode);
        switch (response.getStatusCode()) {
            case 200:
                return Response.ok(response.getBody(), MediaType.APPLICATION_JSON).build();
            case 404:
                return Response.status(404).entity(response.getBody()).build();
            default:
                return Response.status(500).build();
        }
    }

    /**
     *
     * @param suburb
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({"USER", "ADMIN"})
    @Path("postcode/{suburb}")
    public Response searchPostcodes(@PathParam("suburb") String suburb) {

        SerialResponse response = postalDataBean.searchPostcodes(suburb);
        switch (response.getStatusCode()) {
            case 200:
                return Response.ok(response.getBody(), MediaType.APPLICATION_JSON).build();
            case 404:
                return Response.status(404).entity(response.getBody()).build();
            default:
                return Response.status(500).build();
        }
    }

    /**
     *
     * @param suburb
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //@RolesAllowed({"USER", "ADMIN"})
    @Path("suburb/detail/{suburb}")
    public Response searchSuburbDetail(@PathParam("suburb") String suburb) {

        SerialResponse response = postalDataBean.searchSuburbDetail(suburb);
        switch (response.getStatusCode()) {
            case 200:
                return Response.ok(response.getBody(), MediaType.APPLICATION_JSON).build();
            case 404:
                return Response.status(404).entity(response.getBody()).build();
            default:
                return Response.status(500).build();
        }
    }

    /**
     *
     * @param quantity
     * @param from
     * @param to
     * @param type
     * @return
     */
    @POST
    @Path("calculate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public double calculateShippingCost(@FormParam("quantity") int quantity,
            @FormParam("from") int from,
            @FormParam("to") int to,
            @FormParam("type") String type) {
        String serviceCode = "";
        if (type.equals("normal")) {
            serviceCode = "AUS_PARCEL_REGULAR";
        } else if (type.equals("express")) {
            serviceCode = "AUS_PARCEL_EXPRESS";
        } else {
            throw new RuntimeException("Invalid service code");
        }

        double cost = 0;
        while (quantity > 0) {
            cost += postalFeeBean.calculatePostageCost(Math.max(quantity, 30), from, to, serviceCode);
            quantity -= 30;
        }

        return cost;
    }
}
