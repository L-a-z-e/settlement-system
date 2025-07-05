package com.laze.settlementsystem.detail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementDetailReader implements ItemReader<KeyAndCount>, StepExecutionListener {

    private Iterator<Map.Entry<Key, Long>> iterator;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final Map<Key, Long> snapshots= (ConcurrentHashMap<Key, Long>)jobExecution.getExecutionContext().get("snapshots");
        iterator = snapshots.entrySet().iterator();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return StepExecutionListener.super.afterStep(stepExecution);
    }

    @Override
    public KeyAndCount read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!iterator.hasNext())
            return null;

        final Map.Entry<Key, Long> map = iterator.next();
        return new KeyAndCount(map.getKey(), map.getValue());
    }
}
