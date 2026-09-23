package ru.mealcard.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mealcard.config.Config;
import ru.mealcard.utils.filename.FilenameGeneratorUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class FilenameGeneratorUtilTest {

    private final FilenameGeneratorUtil generator = FilenameGeneratorUtil.getInstance();
    private final Pattern pattern = Pattern.compile(
            "Z(\\d{3})(\\d{3})\\.GLAER_ENROLL\\1\\2(\\d+)\\.(\\d{3})");

    private Matcher matcher;
    private String filename;

    @BeforeEach
    void setUp() {
        filename = generator.generate("001", "032", "GLAER");
        matcher = pattern.matcher(filename);
    }

    @Test
    void testFilenameMatchesPattern() {
        assertThat(matcher.matches()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"001,032", "1,32", "999,999", "42,7"})
    void testBankAndBranchPaddedToThreeDigits(String bank, String branch) {
        String name = generator.generate(bank, branch, "GLAER");
        Matcher m = pattern.matcher(name);

        assertThat(m.matches()).isTrue();
        assertThat(m.group(1)).isEqualTo(String.format("%03d", Integer.parseInt(bank)));
        assertThat(m.group(2)).isEqualTo(String.format("%03d", Integer.parseInt(branch)));
    }

    @Test
    void testJulianDateIsToday() {
        int julian = LocalDate.now(ZoneId.of(Config.getInstance().getZone())).getDayOfYear();

        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(4)).isEqualTo(String.format("%03d", julian));
    }

    @Test
    void testSequenceIncrementsOnEachCall() {
        Matcher first = pattern.matcher(filename);
        Matcher second = pattern.matcher(generator.generate("001", "032", "GLAER"));

        assertThat(first.matches() && second.matches()).isTrue();
        assertThat(Integer.parseInt(second.group(3)))
                .isEqualTo(Integer.parseInt(first.group(3)) + 1);
    }

    @Test
    void testFilenameContainsAesName() {
        String name = generator.generate("001", "032", "ABC");

        assertThat(name).contains("ABC").contains("ENROLL");
    }

    @Test
    void testFilenameStartsWithZ() {
        assertThat(filename).startsWith("Z");
    }

    @Test
    void testFilenameContainsEnrollConstant() {
        assertThat(filename).contains("_ENROLL");
    }
}