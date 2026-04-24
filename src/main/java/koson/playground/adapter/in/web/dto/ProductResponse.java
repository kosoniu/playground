package koson.playground.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.util.UUID;

public record ProductResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name,
        @JsonProperty("price") BigInteger price
) {

}
