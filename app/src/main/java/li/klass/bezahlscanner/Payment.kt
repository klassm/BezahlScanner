package li.klass.bezahlscanner

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.joda.time.DateTime

class Payment(val name: String,
              val iban: String,
              val bic: String,
              val amount: String,
              val currency: String = "EUR",
              val reason: String,
              val date: DateTime
) {
    override fun toString(): String = ToStringBuilder.reflectionToString(this)
    override fun equals(other: Any?): Boolean = EqualsBuilder.reflectionEquals(this, other)
    override fun hashCode(): Int = HashCodeBuilder.reflectionHashCode(this)
}
