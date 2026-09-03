package org.example.models.gru;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "GRU_VISTA_TAB", schema = "GRU")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GruVistaTab {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gru_vista_seq")
    @SequenceGenerator(name = "gru_vista_seq", sequenceName = "GRU.GRU_VISTA_SEQ")
    @Column(name = "ID")
    private Long id;

    @Column(name = "SYSTEMACCOUNT", nullable = false, length = 30)
    private String systemAccount;

    @Column(name = "CURRENCY", nullable = false, length = 3)
    @Builder.Default
    private String currency = "222";

    @Column(name = "XALFA", length = 20)
    private String xalfa;

    @Column(name = "OPERATION", nullable = false, length = 2)
    private String operation;

    @CreationTimestamp
    @Column(name = "TIME_STAMP", insertable = false, updatable = false)
    private LocalDateTime timeStamp;

    @Column(name = "POM_ID", nullable = false)
    private Long pomId;

    @Column(name = "UTERRARIO", nullable = false, length = 18)
    private String uterario;

    @Column(name = "OLDTBAL")
    private String oldTbal;

    @Column(name = "NEWTBAL")
    private String newTbal;

    @Column(name = "ADD_INFO", length = 100)
    private String addInfo;

    @Column(name = "FILE_ID", nullable = false)
    private Long fileId;

    @Column(name = "FOC_STATUS", nullable = false, length = 10)
    @Builder.Default
    private String focStatus = "WAIT";

    @Column(name = "FOC_TS")
    private LocalDateTime focTs;

    @Column(name = "FOC_TYPE", length = 20)
    private String focType;
}