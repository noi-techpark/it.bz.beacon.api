package it.bz.beacon.api.scheduledtask.inforeplication;

public class DbWriteException extends RuntimeException {
	private static final long serialVersionUID = 4826241254833699661L;

	public DbWriteException(Throwable cause) {
        super(cause);
    }
}
