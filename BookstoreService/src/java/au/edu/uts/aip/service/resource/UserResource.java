package au.edu.uts.aip.service.resource;

import au.edu.uts.aip.domain.dto.AddressDTO;
import au.edu.uts.aip.domain.entity.User;
import au.edu.uts.aip.domain.exception.ActivationException;
import au.edu.uts.aip.domain.remote.UserRemote;
import au.edu.uts.aip.domain.validation.ValidationResult;
import au.edu.uts.aip.domain.dto.UserDTO;
import au.edu.uts.aip.domain.exception.PasswordResetException;
import au.edu.uts.aip.domain.remote.AdminRemote;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("user")
@RequestScoped
public class UserResource {

    @EJB
    private UserRemote userBean;

    @EJB
    private AdminRemote adminBean;
    
    @Context
    private HttpServletRequest request;
    
    @Context
    private SecurityContext securityContext;

    /**
     * Retrieve the current authenticated user Banned accounts or not logged in user will not be
     * able to retrieve their account detail
     *
     * @return HTTP status code OK and user detail, without password attached
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"USER", "ADMIN", "VERIFYING USER", "VERIFIED USER", "INACTIVATED"})
    public Response get() {
        UserDTO user = userBean.getUser(request.getUserPrincipal().getName());
        return Response.status(Response.Status.OK).entity(user).build();
    }

    /**
     * Create a new user account
     *
     * @param username
     * @param password
     * @param email
     * @param fullname
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@FormParam("username") String username,
                         @FormParam("password") String password,
                         @FormParam("email") String email,
                         @FormParam("fullname") String fullname) {
        try {
            UserDTO user = new UserDTO();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullname(fullname);

            ValidationResult result = userBean.createUser(user, password);
            if (result == null) {
                return Response.status(Response.Status.ACCEPTED).build();
            } else {
                return Response.status(Response.Status.CONFLICT).entity(result.toJson()).build();
            }
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("address")
    @RolesAllowed({"USER", "VERIFYING USER"})
    public Response updateAddress(AddressDTO addressDTO){
        String username = securityContext.getUserPrincipal().getName();
        userBean.updateAddress(addressDTO, username);
        return Response.ok().build();
    }

    /**
     * Activate user's account
     *
     * @param token
     * @param username
     * @return
     */
    @POST
    @Path("activate")
    public Response activateAccount(@FormParam("token") String token, 
                                    @FormParam("username") String username) {
        try {
            userBean.activateAccount(token, username);
            return Response.ok().build();
        } catch (ActivationException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    /**
     * Reset user's password
     * @param token
     * @param username
     * @param newPassword
     * @return 
     */
    @POST
    @Path("reset")
    public Response resetPassword(@FormParam("token") String token,
                                @FormParam("username") String username,
                                @FormParam("newPassword") String newPassword){
        try {
            userBean.resetPassword(token, username, newPassword);
            return Response.ok().build();
        } catch (PasswordResetException ex){
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }

    /**
     * Retrieve a list of user accounts with filter Only administrators can access this resource
     *
     * @param roles
     * @param username
     * @param fullname
     * @param email
     * @param offset
     * @param limit
     * @return
     */
    @GET
    @Path("list")
    @RolesAllowed({"ADMIN"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccounts(@QueryParam("roles") String roles,
                                @QueryParam("username") String username,
                                @QueryParam("fullname") String fullname,
                                @QueryParam("email") String email,
                                @QueryParam("offset") int offset,
                                @QueryParam("limit") int limit) {
        String[] rolesName = roles.split(",");
        List<UserDTO> usersDTO = userBean.findUsers(rolesName, username, fullname, email, offset, limit);

        return Response.status(Response.Status.OK).entity(usersDTO.toArray(new UserDTO[0]))
                .build();
    }

    @POST
    @Path("ban/{username}")
    @RolesAllowed({"ADMIN"})
    public Response banAccount(@PathParam("username") String username) {
        adminBean.banAccount(username);
        return Response.ok().build();
    }

    @POST
    @Path("unban/{username}")
    @RolesAllowed({"ADMIN"})
    public Response unbanAccount(@PathParam("username") String username) {
        adminBean.unbanAccount(username);
        return Response.ok().build();
    }
}
