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

@Path("postal")
public class PostalResource {

    @EJB
    private PostalFeeRemote postalFeeBean;
    @EJB
    private PostalDataRemote postalDataBean;

    // <editor-fold defaultstate="collapsed" desc="test methods for postal fee API">
    @Deprecated
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("calculate_reg_test")
    public Response calculateRegularTest() {
        postalFeeBean.calculatePostageCost(32, 800, 9999, "AUS_PARCEL_REGULAR");
        return Response.ok().build();
    }

    @Deprecated
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("calculate_exp_test")
    public Response calculateExpressTest() {
        postalFeeBean.calculatePostageCost(32, 800, 9999, "AUS_PARCEL_EXPRESS");
        return Response.ok().build();
    }
    
    // </editor-fold>

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
    
    @POST
    @Path("calculate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public double calculateShippingCost(@FormParam("quantity") int quantity,
                                        @FormParam("from") int from,
                                        @FormParam("to") int to,
                                        @FormParam("type") String type) {
        if (type.equals("normal")){
            return postalFeeBean.calculatePostageCost(quantity, from, to, "AUS_PARCEL_REGULAR");
        } else if (type.equals("express")){
            return postalFeeBean.calculatePostageCost(quantity, from, to, "AUS_PARCEL_EXPRESS");
        } else {
            return -1.0;
        }
    }
}
