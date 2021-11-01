package io.licitat.hiberplay.mapping;

interface Mapper<FROM, TO> {
    Runnable arm(FROM source, TO target);
}
