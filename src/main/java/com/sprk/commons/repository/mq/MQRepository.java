package com.sprk.commons.repository.mq;

import com.sprk.commons.entity.mq.JobModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MQRepository extends JpaRepository<JobModel, Long> {
}
