package io.licitat.hiberplay.persistence;

import javax.persistence.SequenceGenerator;

@SequenceGenerator(
    name = DasGenerator.NAME,
    sequenceName = DasGenerator.SEQUENCE_NAME,
    initialValue = DasGenerator.INITIAL_VALUE, allocationSize = DasGenerator.ALLOCATION_SIZE
)
public interface DasGenerator {
    String NAME = "EntityIdGenerator";
    String SEQUENCE_NAME = "EntityIdSequence";
    int INITIAL_VALUE = 1_000_000;
    int ALLOCATION_SIZE = 25;
}
