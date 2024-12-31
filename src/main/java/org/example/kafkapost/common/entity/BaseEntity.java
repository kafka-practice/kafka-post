package org.example.kafkapost.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Column(name = "use_yn", nullable = false)
    private boolean useYn = true;

    @Column(name = "create_code", updatable = false)
    private String createCode;

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "modify_code")
    private String modifyCode;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "delete_code")
    private String deleteCode;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @PrePersist
    public void prePersist(){
        this.createDate = LocalDateTime.now();
        this.createCode = "kafka-user";
        this.useYn = true;
    }

    @PreUpdate
    public void markModified(){
        this.modifyDate = LocalDateTime.now();
        this.modifyCode = "kafka-user";
    }

    public void markDeleted(){
        this.deleteDate = LocalDateTime.now();
        this.deleteCode = "kafka-user";
        this.useYn = false;
    }

    public void markReuse(){
        this.deleteDate = null;
        this.deleteCode = null;
        this.useYn = true;
    }
}
