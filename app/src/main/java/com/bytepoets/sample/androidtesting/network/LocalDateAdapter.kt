import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class LocalDateAdapter : JsonAdapter<LocalDate>() {
    private val dateAdapter = Rfc3339DateJsonAdapter()

    companion object {
        private val FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    override fun toJson(writer: JsonWriter, value: LocalDate?) {
        dateAdapter.toJson(
            writer,
            value?.let { Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant()) }
        )
    }

    override fun fromJson(reader: JsonReader): LocalDate? {
        return dateAdapter
            .fromJson(reader)
            ?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
    }
}
