package com.github.vzakharchenko.dynamic.orm.core.pk;

/**
 *
 */
public enum PrimaryKeyGenerators {
    DEFAULT(null),
    INTEGER(PKGeneratorInteger.getInstance()),
    LONG(PKGeneratorLong.getInstance()),
    UUID(UUIDPKGenerator.getInstance()),
    SEQUENCE(PKGeneratorSequence.getInstance());

    private PKGenerator<?> pkGenerator;

    PrimaryKeyGenerators(PKGenerator<?> pkGenerator) {
        this.pkGenerator = pkGenerator;
    }

    public PKGenerator<?> getPkGenerator() {
        return pkGenerator;
    }
}
