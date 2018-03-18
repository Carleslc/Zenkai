package ai.zenkai.zenkai

import org.threeten.bp.Instant
import java.util.Date

actual class DateTime(val instant: Instant): Comparable<DateTime> {
    
    fun toDate() = Date(instant.toEpochMilli())
    
    actual override fun toString() = instant.toString()
    
    override fun compareTo(other: DateTime) = instant.compareTo(other.instant)
    
    actual companion object Factory {
        
        actual fun now() = DateTime(Instant.now())
        
        actual fun fromMillis(epochMillis: Long) = DateTime(Instant.ofEpochMilli(epochMillis))
    
        fun fromDate(date: Date) = fromMillis(date.time)
        
    }
    
}

/** Expects toString() default format */
actual fun String.parseDateTime() = DateTime(Instant.parse(this))