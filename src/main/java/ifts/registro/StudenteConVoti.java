package ifts.registro;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudenteConVoti extends Studente {
    
    private Map<String,List<Integer>> voti;
    
    public StudenteConVoti() {}
    
    public StudenteConVoti(
        int matricola,
        String cognome,
        String nome,
        List<String> materie
    ) {
        super(matricola,cognome,nome);
        this.voti = new HashMap<String,List<Integer>>();
        for(int i=0; i<materie.size(); i++) {
            String materia = materie.get(i);
            this.voti.put(
                materia,
                new ArrayList<Integer>()
            );
        }
    }
        
    @JsonProperty
    public Map<String,List<Integer>> getVoti() { return this.voti; }
    
}
