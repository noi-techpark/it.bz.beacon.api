package it.bz.beacon.api.scheduledtask.inforeplication.value;

abstract public class BaseValueGenerator<T> {

    public T randomValue() {
        return generate();
    }

    private T generate() {
        T value = generateRandomValue();
        if (alreadyExists(value)) {
            return generate();
        }

        return value;
    }

    abstract protected T generateRandomValue();

    abstract protected boolean alreadyExists(T value);

}
