package li.klass.bezahlscanner

import org.joda.time.DateTime

class DateTimeProvider {
    fun now(): DateTime {
        return DateTime()
    }
}
