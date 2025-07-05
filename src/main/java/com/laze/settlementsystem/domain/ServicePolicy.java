package com.laze.settlementsystem.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ServicePolicy {
    A( 1L, "/L-a-z-e/settlement-system/services/a", 10 ),
    B( 2L, "/L-a-z-e/settlement-system/services/b", 10 ),
    C( 3L, "/L-a-z-e/settlement-system/services/c", 10 ),
    D( 4L, "/L-a-z-e/settlement-system/services/d", 15 ),
    E( 5L, "/L-a-z-e/settlement-system/services/e", 15 ),
    F( 6L, "/L-a-z-e/settlement-system/services/f", 10 ),
    G( 7L, "/L-a-z-e/settlement-system/services/g", 10 ),
    H( 8L, "/L-a-z-e/settlement-system/services/h", 10 ),
    I( 9L, "/L-a-z-e/settlement-system/services/i", 10 ),
    J( 10L, "/L-a-z-e/settlement-system/services/j", 10 ),
    K( 11L, "/L-a-z-e/settlement-system/services/k", 10 ),
    L( 12L, "/L-a-z-e/settlement-system/services/l", 12 ),
    M( 13L, "/L-a-z-e/settlement-system/services/m", 12 ),
    N( 14L, "/L-a-z-e/settlement-system/services/n", 12 ),
    O( 15L, "/L-a-z-e/settlement-system/services/o", 10 ),
    P( 16L, "/L-a-z-e/settlement-system/services/p", 10 ),
    Q( 17L, "/L-a-z-e/settlement-system/services/q", 10 ),
    R( 18L, "/L-a-z-e/settlement-system/services/r", 10 ),
    S( 19L, "/L-a-z-e/settlement-system/services/s", 10 ),
    T( 20L, "/L-a-z-e/settlement-system/services/t", 10 ),
    U( 21L, "/L-a-z-e/settlement-system/services/u", 10 ),
    V( 22L, "/L-a-z-e/settlement-system/services/v", 10 ),
    W( 23L, "/L-a-z-e/settlement-system/services/w", 19 ),
    X( 24L, "/L-a-z-e/settlement-system/services/x", 19 ),
    Y( 25L, "/L-a-z-e/settlement-system/services/y", 19 ),
    Z( 26L, "/L-a-z-e/settlement-system/services/z", 19 );

    private final Long id;
    private final String url;
    private final Integer fee;

    public static ServicePolicy findByUrl(String url) {
        return Arrays.stream(values())
                .filter(it -> it.url.equals(url))
                .findFirst()
                .orElseThrow();
    }

    public static ServicePolicy findById(Long id) {
        return Arrays.stream(values())
                .filter(it -> it.id.equals(id))
                .findFirst()
                .orElseThrow();
    }
}
