package org.example.models.pom;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "FILE", schema = "POM")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PomFile {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pom_file_seq")
    @SequenceGenerator(name = "pom_file_seq", sequenceName = "POM.FILE_SEQ")
    @Column(name = "ID")
    private Long id;

    @CreationTimestamp
    @Column(name = "INS_TIME", insertable = false, updatable = false)
    private LocalDateTime insTime;

    @Column(name = "FILENAME", nullable = false)
    private String filename;

    @Column(name = "FULLPATH", length = 500)
    private String fullPath;

    @Column(name = "SENDER", length = 10)
    @Builder.Default
    private String sender = "001 032";

    @Column(name = "FILE_COMMENT", length = 1000)
    private String fileComment;

    @UpdateTimestamp
    @Column(name = "UPD_TIME")
    private LocalDateTime updTime;

    @Column(name = "FILE_STATUS", nullable = false, length = 20)
    private String fileStatus;

    @Column(name = "ULI_DATE", length = 10)
    private String uliDate;
}