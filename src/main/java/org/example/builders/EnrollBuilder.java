package org.example.builders;

import org.example.models.gru.GruVistaTab;
import org.example.models.pom.PomUnit;
import org.example.models.pom.PomUnitError;
import org.example.service.impl.visitor.dto.BodyDTO;
import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EnrollBuilder {

    private final Faker faker = new Faker();

    public PomUnit buildPomUnitHeader(Long fileId, String line, boolean isValid) {

        return PomUnit.builder()
                .fileId(fileId)
                .pomType("101")
                .status(isValid ? "SUCCESS" : "ERROR")
                .unitValue(line)
                .addValue(isValid ? null : "Invalid header format")
                .build();
    }

    public PomUnit buildPomUnitTrailer(Long fileId, String line, boolean isValid) {

        return PomUnit.builder()
                .fileId(fileId)
                .pomType("108")
                .status(isValid ? "SUCCESS" : "ERROR")
                .unitValue(line)
                .addValue(isValid ? null : "Invalid trailer format")
                .build();
    }


    public PomUnit buildPomUnitBody(BodyDTO dto, Long fileId, String line, boolean isValid) {


        return PomUnit.builder()
                .fileId(fileId)
                .pomType("106")
                .status(isValid ? "SUCCESS" : "ERROR")
                .unitValue(line)
                .addValue(isValid ? null : getErrorMessage(dto, isValid))
                .build();


    }


    public GruVistaTab buildGru(BodyDTO dto, Long fileId, LocalDateTime procTime, String procType) {
        return GruVistaTab.builder()
            .systemAccount(dto.getAccount())
            .currency("222")
            .xalfa(dto.getAmount())
            .operation(dto.getOp_type())
            .pomId(null)
            .uterario(generateUterario())
            .addInfo(getAddInfo(dto.getOp_type()))
            .fileId(fileId)
            .focStatus("WAIT")
            .focTs("IN-TIME".equals(procType) ? procTime : null)
            .focType(procType)
            .build();
    }

    public PomUnitError buildError(BodyDTO dto, Long fileId, String line, boolean isValid) {
        return PomUnitError.builder()
                .unitId(null)
                .errorSeq(1)
                .errorCode(getErrorCode(dto, isValid))
                .errorField(line)
                .errorMsg(getErrorMessage(dto, isValid))
                .fileId(fileId)
                .build();
    }




    private String getErrorCode(BodyDTO dto, boolean isValid) {
        if (!isValid) {

            return "ERR_FILE_INVALID";

        } else if (dto.getAccount().isEmpty()) {

            return "ERR_ACC";
        }

        return "ERR_OP";

    }

    private String getErrorMessage(BodyDTO dto, boolean isValid) {
        if (!isValid) {
            return "file trailer or header is invalid";

        }

        if (dto.getAccount().isEmpty()) {
            return "account is empty";
        }
        return "invalid operation type: " + dto.getOp_type();
    }

    private String getAddInfo(String op) {
        return switch (op) {
            case "ZR" -> "Обнуление счёта";
            case "CR" -> "Списание счёта";
            case "DR" -> "Зачисление насчёт";
            default -> null;
        };
    }

    private String generateUterario() {

        return faker.number().digits(18);
    }
}

