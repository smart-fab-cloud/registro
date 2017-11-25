package ifts.registro;

import java.util.ArrayList;
import java.util.List;
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

@Path("/registro")
public class Registro {
    
    private List<String> materie;
    private List<StudenteConVoti> registro;
    
    public Registro(List<String> materie) {
        this.materie = materie;
        this.registro = new ArrayList<StudenteConVoti>();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void aggiungiStudente(Studente s) {
        // Costruisce un record studente-voti "sv" a partire da "s"
        StudenteConVoti sv = new StudenteConVoti(
            s.getMatricola(),
            s.getCognome(),
            s.getNome(),
            this.materie
        );
        // Aggiunge "sv" al "registro"
        this.registro.add(sv);
    }
    
    // Metodo privato per recuperare l'indice dello studente avente la 
    // "matricola" specifica nel "registro"
    private int indiceStudente(int matricola) {
        for (int i=0; i<this.registro.size(); i++)
            if (this.registro.get(i).getMatricola()==matricola)
                return i;
        return -1;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{matricola}")
    public StudenteConVoti recuperaStudente(
        @PathParam("matricola") int matricola
    ) {
        // Recupera l'indice dello studente desiderato
        int i = indiceStudente(matricola);
        // Se lo studente è presente
        if (i>-1)
            // ne restituisce la rappresentazione
            return this.registro.get(i);
        // Altrimenti, ritorna "null"
        return null;
    }
    
    @PUT
    @Path("/{matricola}")
    public void aggiungiVoto(
        @PathParam("matricola") int matricola,
        @QueryParam("materia") String materia,
        @QueryParam("voto") int voto
    ) {
        // Recupera l'indice dello studente desiderato
        int i = indiceStudente(matricola);
        // Se lo studente è presente
        if (i>-1) {
            // recupera la lista dei voti nella materia indicata
            List<Integer> votiMateria = 
                this.registro.get(i).getVoti().get(materia);
            // Verifica che 
            // - "voto" sia un numero tra 1 e 10 e che 
            // - "materia" sia una materia tra quelle registrabili
            //   NB: Se "materia" non fosse tra quelle registrabili,
            //   "votiMateria" sarebbe "null"
            // Se così è, aggiunge il nuovo "voto" tra quelli registrati
            if (voto>=1 && voto<=10 && votiMateria!=null)
                votiMateria.add(voto);
        }
    }
    
    @DELETE
    @Path("/{matricola}")
    public void eliminaStudente(
        @PathParam("matricola") int matricola
    ) {
        // Recupera l'indice dello studente desiderato
        int i = indiceStudente(matricola);
        // Se lo studente è presente
        if (i>-1)
            // lo elimina dal registro
            this.registro.remove(i);
    }
}
