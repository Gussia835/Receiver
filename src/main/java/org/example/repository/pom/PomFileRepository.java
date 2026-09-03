package org.example.repository.pom;

import org.example.models.pom.PomFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PomFileRepository extends JpaRepository<PomFile, Long> {


}
