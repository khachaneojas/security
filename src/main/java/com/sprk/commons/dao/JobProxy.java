package com.sprk.commons.dao;

import com.sprk.commons.entity.mq.JobModel;
import com.sprk.commons.entity.mq.tag.JobStatus;
import com.sprk.commons.entity.mq.tag.JobType;
import com.sprk.commons.entity.mq.tag.ScheduleType;
import com.sprk.commons.repository.mq.MQRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;



@Component
@ConditionalOnProperty(name = "spring.jpa.mq.enabled", havingValue = "true")
@RequiredArgsConstructor
public class JobProxy {
    private final MQRepository repository;

    @Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRED)
    public void addJobInDB(String jobName, String jobDescription, String json, JobType jobType, ScheduleType scheduleType, Instant executeAt, List<DayOfWeek> days, List<Integer> dates) {
        Objects.requireNonNull(jobName);
        Objects.requireNonNull(jobType);
        Objects.requireNonNull(scheduleType);
        Objects.requireNonNull(executeAt);

        List<DayOfWeek> specifiedDays = Optional.ofNullable(days)
                .filter(list -> !list.isEmpty())
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        List<Integer> specifiedDates = Optional.ofNullable(dates)
                .filter(list -> !list.isEmpty())
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .filter(dateInt -> dateInt < 32 && dateInt > 0)
                .distinct()
                .sorted()
                .toList();

        if (Arrays.asList(ScheduleType.SPECIFIED_CUSTOM_DATES, ScheduleType.SPECIFIED_WEEKDAYS).contains(scheduleType)) {
            if (specifiedDates.isEmpty() || specifiedDays.isEmpty()) {
                throw new IllegalArgumentException("Cannot proceed without specifying custom dates, if schedule-type is " + scheduleType.name() + ".");
            }
        }

        if (Arrays.asList(JobType.EMAIL, JobType.EXAM_STATUS_CHANGE).contains(jobType)) {
            if (null == json) {
                throw new IllegalArgumentException("Cannot proceed without specifying json-data, if job-type is " + jobType.name() + ".");
            }
        }

        JobModel model = JobModel.builder()
                .name(jobName)
                .description(jobDescription)
                .jsonData(json)
                .status(JobStatus.QUEUED)
                .jobType(jobType)
                .lastRanAt(null)
                .lastRanBy(null)
                .executeAt(executeAt) // UTC (04:30-AM) --- IST (10:00-AM)
                .scheduleType(scheduleType)
                .specifiedDates(specifiedDates.isEmpty() ? null : Arrays.toString(dates.toArray()))
                .weekDays(specifiedDays.isEmpty() ? null : Arrays.toString(days.toArray()))
                .build();

        repository.save(model);
    }
}
