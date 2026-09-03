package org.example.service.dao;

import lombok.RequiredArgsConstructor;
import org.example.models.gru.GruVistaTab;
import org.example.models.pom.PomFile;
import org.example.models.pom.PomUnit;
import org.example.models.pom.PomUnitError;
import org.example.repository.gru.GruVistaTabRepository;
import org.example.repository.pom.PomFileRepository;
import org.example.repository.pom.PomUnitErrorRepository;
import org.example.repository.pom.PomUnitRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SaverDAO {
    private final PomUnitRepository pomUnitRepository;
    private final PomUnitErrorRepository pomUnitErrorRepository;
    private final PomFileRepository pomFileRepository;

    private final GruVistaTabRepository gruVistaTabRepository;


    @Transactional("pomTransactionManager")
    public PomFile savePomFile(PomFile pomFile) {
        return pomFileRepository.save(pomFile);
    }


    @Transactional("gruTransactionManager")
    public void saveGRU(GruVistaTab gru) {
        gruVistaTabRepository.save(gru);
    }


    @Transactional("pomTransactionManager")
    public void saveError(PomUnitError error) {
        pomUnitErrorRepository.save(error);
    }


    @Transactional("pomTransactionManager")
    public PomUnit savePomUnit(PomUnit pomUnit) {


        return pomUnitRepository.saveAndFlush(pomUnit);
    }


}
