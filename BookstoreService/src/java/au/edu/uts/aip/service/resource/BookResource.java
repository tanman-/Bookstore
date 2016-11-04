package au.edu.uts.aip.service.resource;

import au.edu.uts.aip.domain.entity.Book;
import au.edu.uts.aip.domain.remote.BookstoreRemote;
import au.edu.uts.aip.domain.dto.BookDTO;
import au.edu.uts.aip.domain.dto.BookSaleDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("book")
public class BookResource {

    @EJB
    private BookstoreRemote bookstoreBean;

    @Context
    private SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("latest")
    public Response getLatestBooks(@QueryParam("offset") int offset,
                                   @QueryParam("limit") int limit) {

        List<BookDTO> booksDTO = bookstoreBean.getLatestBooks(offset, limit);
        return Response.status(Response.Status.OK).entity(booksDTO.toArray(new BookDTO[0])).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@QueryParam("isbn10") String isbn10,
                            @QueryParam("isbn13") String isbn13,
                            @QueryParam("title") String title) {
        BookDTO bookDTO = bookstoreBean.getSingleBook(isbn10, isbn13, title);

        return Response.ok(bookDTO).build();
    }

    @PUT
    @Path("sales")
    @RolesAllowed({"ADMIN", "VERIFIED USER"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSales(BookDTO salesData) {
        BookDTO result = bookstoreBean.updateSale(securityContext.getUserPrincipal().getName(), salesData);
        return Response.ok().entity(result).build();
    }
    
    @POST
    @Path("sales")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSales(List<BookSaleDTO> salesDTO){
        List<BookSaleDTO> result = bookstoreBean.getSales(salesDTO);
        return Response.ok().entity(result).build();
    }
}
