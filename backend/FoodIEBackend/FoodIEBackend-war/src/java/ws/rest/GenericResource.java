package ws.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ejb.session.stateless.ActorUserControllerLocal;
import entity.ActorUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import util.exception.InvalidLoginCredentialException;
import util.security.JWTManager;


@Path("GenericResource")
public class GenericResource {

    @Context
    private UriInfo context;

    private final ActorUserControllerLocal actorUserControllerLocal;
    
    private Gson gson;
    
    private JWTManager jWTManager;

    public GenericResource() {
        actorUserControllerLocal = lookupActorUserControllerLocal();
        gson = new Gson();
        jWTManager = new JWTManager();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "HelloWorld")
    public String HelloWorld() {
        List<String> lsName = new ArrayList<>();
        lsName.add("test1");
        lsName.add("test2");
        lsName.add("test3");

        String jsonStr = gson.toJson(lsName);
        return jsonStr;
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path(value = "RetrieveAllActorUser")
//    public String RetrieveAllActorUser() {
//        List<ActorUser> lsActorUser =  actorUserControllerLocal.retrieveAllActorUser();
//        
//        String jsonStr = gson.toJson(lsActorUser);
//        return jsonStr;
//    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path(value = "Login")
    public Response Login(@FormParam("email") String email,
                        @FormParam("password") String password) {
        String jsonStr;
        try {
            // try to login
            ActorUser actorUser = actorUserControllerLocal.actorUserLogin(email, password);
            actorUser.setPassword(null);
            actorUser.setSalt(null);
            
            jsonStr = gson.toJson(actorUser);
            
            // login succesfully
            // parse the result in jwt
            String response = jWTManager.createJWT("weihan1394@gmail.com", jsonStr, actorUser.getFirstName(), 1000000);
            
            jsonStr = gson.toJson(response);
            return Response.status(Response.Status.OK).entity(jsonStr).build();
        } 
        catch (InvalidLoginCredentialException ex) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("message", ex.getMessage());
            jsonStr = jsonObject.toString();
            return Response.status(Response.Status.FORBIDDEN).entity(jsonStr).build();
        }
        
    }
    
    private ActorUserControllerLocal lookupActorUserControllerLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            // weihan: naming convention to inject ejb
            // https://docs.oracle.com/javaee/6/tutorial/doc/gipjf.html
            return (ActorUserControllerLocal)c.lookup("java:global/FoodIEBackend/FoodIEBackend-ejb/ActorUserController!ejb.session.stateless.ActorUserControllerLocal");
        }
        catch (NamingException namingException) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", namingException);
            throw new RuntimeException(namingException);
        }
    }
}
