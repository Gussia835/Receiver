package ru.mealcard.utils.filename;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import ru.mealcard.Base;

import java.time.LocalDate;
import java.time.ZoneId;

public class FilenameGeneratorUtil extends Base {

    private LocalDate currentDay = LocalDate.now(ZoneId.of(getConfig().getZone()));
    private int seq = 1;

    @Getter
    private static final FilenameGeneratorUtil instance = new FilenameGeneratorUtil();

    private FilenameGeneratorUtil() {}

    public String generate(String bankCode, String branchCode, String aesName) {
        LocalDate today = LocalDate.now(ZoneId.of(getConfig().getZone()));

        if (!today.equals(currentDay)) {
            currentDay = today;
            seq = 1;
        }

        int N = seq++;
        int yulian = currentDay.getDayOfYear();
        String bank = StringUtils.leftPad(bankCode, 3, '0');
        String branch = StringUtils.leftPad(branchCode, 3, '0');

        return String.format("Z%s%s.%s_ENROLL%s%s%d.%03d",
                bank, branch, aesName,
                bank, branch,
                N, yulian);
    }
}