package koson.playground.domain.valueobject;

import java.math.BigInteger;
import java.util.UUID;

public record Product(
    UUID id,
    String name,
    BigInteger price
) {

}
