package rf.dom.camel;

import org.apache.camel.main.Main;
public class Test {
    public static void main(String... args) throws Exception {
        Main main = new Main();
        int multiplier;
        try {
            multiplier = Integer.parseInt(args[0]);
        } catch (Exception e) {
            multiplier = 1; // default value (without multiplication)
        }
        main.configure().addRoutesBuilder(new ServiceRouter(multiplier));
        main.run();
    }

}

