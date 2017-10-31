package li.bezahl.scanner

import org.joda.time.DateTime

class DateTimeProvider {
    fun now(): DateTime {
        return DateTime()
    }
}
