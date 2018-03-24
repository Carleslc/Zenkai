package ai.zenkai.zenkai

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.util.Date

actual class DateTime(val instant: Instant): Comparable<DateTime> {
    
    fun toDate() = Date(instant.toEpochMilli())
    
    actual override fun toString() = instant.toString()
    
    override fun compareTo(other: DateTime) = instant.compareTo(other.instant)
    
    actual companion object {
    
        actual fun getTimeZone() = ZoneId.systemDefault().id!!
        
        actual fun now() = DateTime(Instant.now())
        
        actual fun fromMillis(epochMillis: Long) = DateTime(Instant.ofEpochMilli(epochMillis))
    
        fun fromDate(date: Date) = fromMillis(date.time)
        
    }
    
}

/** Expects toString() default format */
actual fun String.parseDateTime() = DateTime(Instant.parse(this))