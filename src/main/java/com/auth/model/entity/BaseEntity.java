package com.auth.model.entity;

import com.auth.constant.DateTimeFormatPattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity implements Serializable {

    @CreatedDate
    @JsonFormat(pattern = DateTimeFormatPattern.PATTERN_1)
    protected Date createdAt;

    @LastModifiedDate
    @JsonFormat(pattern = DateTimeFormatPattern.PATTERN_1)
    protected Date updatedAt;

    @CreatedBy
    protected String createdBy;

    @LastModifiedBy
    protected String updatedBy;

}
