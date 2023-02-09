package rf.dom.camel;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.hc.core5.http.HttpStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class ServiceRouter extends RouteBuilder {
    private final String HTTP_SUCCESS_RETURN_VALUE = "Created";
    private final String HTTP_FORBIDDEN_RETURN_VALUE = "Token is missing or invalid";
    final static Properties properties = new Properties();
    static String outputFilepath;
    static String outputFilename;
    static String inputUrl;
    static String tokenValue;
    int multiplier;
    public ServiceRouter() throws IOException {
        //load configs
        properties.load(new FileInputStream("src/main/resources/config.properties"));
        outputFilepath = properties.getProperty("path.outputFilepath");
        outputFilename = properties.getProperty("path.outputFilename");
        inputUrl = properties.getProperty("http.inputUrl");
        tokenValue = properties.getProperty("http.token");
    };
    public ServiceRouter(int multiplier) throws IOException {
        this();
        this.multiplier = multiplier;
    }
    public void configure() {
        from("jetty://" + inputUrl )
                .choice()
                .when(header("Token").isEqualTo(tokenValue))
                    .unmarshal()
                    .json(JsonLibrary.Jackson, ObjectModel[].class)
                    .process(exchange -> {
                        ObjectModel[] objectModels = exchange.getIn().getBody(ObjectModel[].class);
                        log.info("Length of input array : " + objectModels.length);
                        Arrays.stream(objectModels).forEach(
                                e -> e.setSum(e.getSum() * multiplier));
                    })
                    .marshal().bindy(BindyType.Csv, ObjectModel.class)
                    .to("file:"+ outputFilepath +"?fileName=" + outputFilename)
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_CREATED))
                    .setBody(constant(HTTP_SUCCESS_RETURN_VALUE))
                .otherwise()
                    .log(LoggingLevel.WARN, HTTP_FORBIDDEN_RETURN_VALUE)
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.SC_FORBIDDEN))
                    .setBody(constant(HTTP_FORBIDDEN_RETURN_VALUE));
    }
}
