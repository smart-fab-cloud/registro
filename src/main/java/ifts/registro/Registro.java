package ifts.registro;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/registro")
@Produces(MediaType.APPLICATION_JSON)
public class Registro {
    
    private List<String> materie;
    private List<StudenteConVoti> registro;
    
    public Registro(List<String> materie) {
        this.materie = materie;
        registro = new ArrayList<StudenteConVoti>();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response aggiungiStudente(Studente s) {
        // Se la matricola non è positiva
        if(s.getMatricola() <= 0)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("La matricola deve essere positiva.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Se cognome e/o nome sono vuoti
        if(s.getCognome().isEmpty() || s.getNome().isEmpty())
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Cognome e nome non possono essere vuoti.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Se lo studente è già presente nel registro
        if (indiceStudente(s.getMatricola()) > -1)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.CONFLICT)
                        .entity(s.getMatricola() + " già nel registro.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Costruisce un record studente-voti "sv" a partire da "s"
        StudenteConVoti sv = new StudenteConVoti(
            s.getMatricola(),
            s.getCognome(),
            s.getNome(),
            this.materie
        );
        // Aggiunge "sv" al "registro"
        registro.add(sv);
        // e restituisce la sua URI
        URI svUri = UriBuilder.fromResource(Registro.class)
                        .path(String.valueOf(sv.getMatricola()))
                        .build();
        return Response.created(svUri).build();
    }
    
    // Metodo privato per recuperare l'indice dello studente avente la 
    // "matricola" specifica nel "registro"
    private int indiceStudente(int matricola) {
        for (int i=0; i<registro.size(); i++)
            if (registro.get(i).getMatricola()==matricola)
                return i;
        return -1;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{matricola}")
    public Response recuperaStudente(@PathParam("matricola") int matricola) {
        // Recupera l'indice dello studente desiderato
        int i = indiceStudente(matricola);
        
        // Se lo studente non è presente
        if (i == -1)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.NOT_FOUND)
                        .entity(matricola + " non trovato.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Restituisce lo studente desiderato
        return Response.ok(registro.get(i)).build();
    }
    
    @PUT
    @Path("/{matricola}")
    public Response aggiungiVoto(
        @PathParam("matricola") int matricola,
        @QueryParam("materia") Optional<String> materia,
        @QueryParam("voto") Optional<Integer> voto
    ) {
        // Se la matricola è negativa
        if(matricola <= 0)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("La matricola deve essere positiva.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            
        // Se materia o voto non sono indicati
        if(!materia.isPresent() || !voto.isPresent())
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Materia e voto devono essere indicati.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Se la materia non è tra quelle riconosciute
        if(!materie.contains(materia.get()))
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Materia non riconosciuta.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Se il voto non è un intero tra 1 e 10
        if(voto.get() < 1 || voto.get() > 10)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Il voto deve essere compreso tra 1 e 10.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Recupera l'indice dello studente desiderato
        int i = indiceStudente(matricola);
        
        // Se lo studente non è presente
        if (i == -1)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.NOT_FOUND)
                        .entity(matricola + " non trovato.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Se lo studente è presente, aggiunge il nuovo voto alla lista 
        // dei suoi voti nella materia indicata
        List<Integer> voti  = registro.get(i).getVoti().get(materia.get());
        voti.add(voto.get());
        return Response.ok(registro.get(i)).build();
    }
    
    @DELETE
    @Path("/{matricola}")
    public Response eliminaStudente(@PathParam("matricola") int matricola) {
        // Recupera l'indice dello studente da eliminare
        int i = indiceStudente(matricola);
        
        // Se lo studente non è presente
        if (i == -1)
            // Restituisce un messaggio di errore
            return Response.status(Response.Status.NOT_FOUND)
                        .entity(matricola + " non trovato.")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
        
        // Elimina lo studente 
        registro.remove(i);
        // e restituisce "200 OK"
        return Response.ok()
                    .entity(matricola + "eliminato.")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
    }
}
