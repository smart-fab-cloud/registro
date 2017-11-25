package ifts.registro;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class RegistroApp extends Application<RegistroConfig> {
    
    public static void main(String[] args) throws Exception {
        new RegistroApp().run(args);
    }
    
    @Override
    public void run(RegistroConfig c, Environment e) {
        Registro risorsaRegistro = new Registro(c.getMaterie());
        e.jersey().register(risorsaRegistro);
    }
}
