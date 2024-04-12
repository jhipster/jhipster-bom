package tech.jhipster.domain.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.*;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class JSR310DateConvertersTest {

    @Test
    void testLocalDateToDateConverter() {
        LocalDate localDate = LocalDate.now();
        Date date = JSR310DateConverters.LocalDateToDateConverter.INSTANCE.convert(localDate);

        assertEquals(localDate, JSR310DateConverters.DateToLocalDateConverter.INSTANCE.convert(date));
    }

    @Test
    void testDurationToLongConverter() {
        Duration duration = Duration.ofHours(5);
        Long durationInNanos = JSR310DateConverters.DurationToLongConverter.INSTANCE.convert(duration);

        assertEquals(duration, JSR310DateConverters.LongToDurationConverter.INSTANCE.convert(durationInNanos));
    }

    @Test
    void testDateToZonedDateTimeConverter() {
        Date date = new Date();
        ZonedDateTime zonedDateTime = JSR310DateConverters.DateToZonedDateTimeConverter.INSTANCE.convert(date);

        assertEquals(date.toInstant(), JSR310DateConverters.ZonedDateTimeToDateConverter.INSTANCE.convert(zonedDateTime).toInstant());
    }
}
