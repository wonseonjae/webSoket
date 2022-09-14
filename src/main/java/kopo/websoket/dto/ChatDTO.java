package kopo.websoket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ChatDTO {

    private String name;
    private String msg;
    private String date;
}
