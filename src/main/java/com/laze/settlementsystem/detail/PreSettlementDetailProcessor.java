package com.laze.settlementsystem.detail;

import com.laze.settlementsystem.domain.ApiOrder;
import com.laze.settlementsystem.domain.ServicePolicy;
import org.springframework.batch.item.ItemProcessor;

public class PreSettlementDetailProcessor implements ItemProcessor<ApiOrder, Key> {
    @Override
    public Key process(ApiOrder item) throws Exception {

        if(item.getState() == ApiOrder.State.FAIL)
            return null;

        final Long serviceId = ServicePolicy.findByUrl(item.getUrl()).getId();

        return new Key(
                item.getCustomerId(),
                serviceId
        );
    }
}
