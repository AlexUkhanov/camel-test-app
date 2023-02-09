package rf.dom.camel;

import lombok.Getter;
import lombok.Setter;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord( separator = "," )
public class ObjectModel {
    @Getter
    @Setter
    @DataField(pos = 1, required = true)
    private Long id;
    @Getter
    @Setter
    @DataField(pos = 2, required = true)
    private String name;
    @Getter
    @Setter
    @DataField(pos = 3, required = true)
    private Integer sum;
}
