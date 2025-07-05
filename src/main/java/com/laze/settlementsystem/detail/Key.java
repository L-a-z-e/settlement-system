package com.laze.settlementsystem.detail;

import java.io.Serializable;

record Key(Long customerId, Long serviceId) implements Serializable {
}
