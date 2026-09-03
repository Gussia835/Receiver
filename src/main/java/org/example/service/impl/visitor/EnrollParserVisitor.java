package org.example.service.impl.visitor;

import lombok.RequiredArgsConstructor;
import org.example.builders.EnrollBuilder;
import org.example.models.gru.GruVistaTab;
import org.example.models.pom.PomUnit;
import org.example.models.pom.PomUnitError;
import org.example.service.impl.visitor.dto.BodyDTO;
import org.example.service.impl.visitor.dto.HeaderDTO;
import org.example.service.impl.visitor.dto.TrailerDTO;
import org.example.service.dao.SaverDAO;
import org.example.service.impl.visitor.validator.EnrollValidator;
import org.example.utils.visitor.ParserVisitor;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class EnrollParserVisitor implements ParserVisitor {

    private static final int CREATED_START = 2;
    private static final int CREATED_END = 17;

    private static final int PROC_START = 18;
    private static final int PROC_END = 27;

    private static final int PROCESS_TIME_START = 27;
    private static final int PROCESS_TIME_END = 42;


    private static final int FIO_START = 0;
    private static final int FIO_END = 100;

    private static final int ACC_START = 100;
    private static final int ACC_END = 130;

    private static final int TYPE_START = 130;
    private static final int TYPE_END = 132;

    private static final int SUMM_START = 132;
    private static final int SUMM_END = 152;


    private static final int COUNT_START = 10;
    private static final int COUNT_END = 20;



    private static final DateTimeFormatter DT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
    private static final Charset CHARSET = Charset.forName("windows-1251");


    private final EnrollBuilder builder;
    private final SaverDAO saver;
    private final EnrollValidator validator;


    private Long fileId;
    private LocalDateTime procTime;
    private String procType;
    private boolean isValidHeaderTrailer = true;


    public void setContext(Long fileId, boolean isValidHeader, boolean isValidTrailer) {
        this.fileId = fileId;

        this.isValidHeaderTrailer = isValidHeader && isValidTrailer;
    }




    @Override
    public boolean visit(String line) {






        if (validator.isHeaderLine(line)) {

            HeaderDTO headerDTO = parseHeader(line);

            procTime = headerDTO.getProcTime();
            procType = headerDTO.getProcType();


            PomUnit headerEntity = builder.buildPomUnitHeader(fileId, line, isValidHeaderTrailer);
            saver.savePomUnit(headerEntity);

            return true;
        }

        if (validator.isTrailerLine(line)) {

            TrailerDTO trailerDTO = parseTrailer(line);

            PomUnit trailerEntity = builder.buildPomUnitTrailer(fileId, line, isValidHeaderTrailer);
            saver.savePomUnit(trailerEntity);

            return true;
        }


        BodyDTO bodyDTO = parseBody(line);

        boolean isValidBody = isValidHeaderTrailer
                                && validator.validateBody(line);

        PomUnit bodyEntity = builder.buildPomUnitBody(bodyDTO, fileId, line, isValidBody);
        PomUnit savedUnit = saver.savePomUnit(bodyEntity);


        if (isValidBody) {
            GruVistaTab gru = builder.buildGru(bodyDTO, fileId, procTime, procType);
            gru.setPomId(savedUnit.getId());
            saver.saveGRU(gru);
        } else {
            PomUnitError error = builder.buildError(bodyDTO, fileId, line, isValidBody);
            error.setUnitId(savedUnit.getId());
            saver.saveError(error);
        }

        return true;
    }





    private HeaderDTO parseHeader(String line) {

        String createdStr = safeSubstring(line, CREATED_START, CREATED_END);
        String processTimeStr = safeSubstring(line, PROCESS_TIME_START, PROCESS_TIME_END);

        LocalDateTime createdAt = LocalDateTime.parse(createdStr, DT_FORMATTER);
        String procType = safeSubstring(line, PROC_START, PROC_END);
        LocalDateTime procTime = LocalDateTime.parse(processTimeStr, DT_FORMATTER);


        return new HeaderDTO(createdAt, procType, procTime);
    }



    private TrailerDTO parseTrailer(String line) {

        int count = Integer.parseInt(line.substring(COUNT_START, COUNT_END).trim());

        return new TrailerDTO(count);
    }



    private BodyDTO parseBody(String line) {



        String fio = safeSubstring(line, FIO_START, FIO_END);
        String account = safeSubstring(line, ACC_START, ACC_END);
        String typeOp = safeSubstring(line, TYPE_START, TYPE_END);
        String summ = safeSubstring(line, SUMM_START, SUMM_END);

        return new BodyDTO(fio, account, typeOp, summ);
    }


    private String safeSubstring(String line, int start, int end) {
        if (line == null || line.length() <= start) return "";
        int actualEnd = Math.min(line.length(), end);
        return line.substring(start, actualEnd).trim();
    }


}
