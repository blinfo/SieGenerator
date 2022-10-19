package sie.generator;

/**
 *
 * @author Håkan Lidén
 */
public class SieGeneratorException extends RuntimeException {

    public SieGeneratorException() {
    }

    public SieGeneratorException(String message) {
        super(message);
    }

    public SieGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SieGeneratorException(Throwable cause) {
        super(cause);
    }

}
