package ifts.registro;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import java.util.List;
import org.hibernate.validator.constraints.NotEmpty;

public class RegistroConfig extends Configuration {
    
    @NotEmpty
    private List<String> materie;
    
    @JsonProperty
    public List<String> getMaterie() { return this.materie; }
    
}
