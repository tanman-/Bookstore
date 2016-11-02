/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.uts.aip.service.filter;

import java.io.IOException;
import javax.servlet.ServletContext;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Joel Pearson (http://stackoverflow.com/questions/23450494/)
 */
@Provider
public class ResponserHeaderFilter implements ContainerResponseFilter {

    @Context
    private ServletContext servletContext;
    
   @Override
   public void filter(final ContainerRequestContext requestContext,
                      final ContainerResponseContext cres) throws IOException {
      cres.getHeaders().add("Access-Control-Allow-Origin", servletContext.getInitParameter("clientURL"));
      cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, X-Requested-With");
      cres.getHeaders().add("Access-Control-Allow-Credentials", "true");
      cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
      cres.getHeaders().add("Access-Control-Max-Age", "1209600");
   }

}