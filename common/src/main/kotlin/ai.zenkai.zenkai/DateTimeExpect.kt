package ai.zenkai.zenkai

const val DATE_FORMAT = "yyyy-MM-ddTHH:mm:ssX" // X: zone-offset, 'Z' for zero (ISO_INSTANT)

/** Location-aware timestamp */
expect class DateTime: Comparable<DateTime> {
    
    override fun toString(): String
    
    companion object Factory {
        
        fun now(): DateTime
        
        fun fromMillis(epochMillis: Long): DateTime
        
    }
    
}

/** Expects toString() default format */
expect fun String.parseDateTime(): DateTime
