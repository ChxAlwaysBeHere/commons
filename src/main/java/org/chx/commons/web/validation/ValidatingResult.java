package org.chx.commons.web.validation;

/**
 * target validating result
 *
 * @author chenxi
 * @date 2019-11-12
 */
public class ValidatingResult {

    private final static ValidatingResult VALID = new ValidatingResult(true);

    private boolean valid;

    private String message;

    public ValidatingResult(boolean valid) {
        this(valid, null);
    }

    public ValidatingResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static ValidatingResult valid() {
        return VALID;
    }

    public static ValidatingResult invalid(String message) {
        return new ValidatingResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
