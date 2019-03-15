package it.bz.beacon.api.scheduledtask.inforeplication.value;

public class EddystoneValue {
    private String namespace;
    private String instanceId;

    public EddystoneValue(String namespace, String instanceId) {
        this.namespace = namespace;
        this.instanceId = instanceId;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
