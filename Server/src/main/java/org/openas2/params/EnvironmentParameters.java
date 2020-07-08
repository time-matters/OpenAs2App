package org.openas2.params;

public class EnvironmentParameters extends ParameterParser {
    public EnvironmentParameters() {
        super();
    }

    public void setParameter(String key, String value) throws InvalidParameterException {
        throw new InvalidParameterException("Set not supported", this, key, value);
    }

    public String getParameter(String key) throws InvalidParameterException {
        if (key != null) {
            return System.getenv(key);
        } else {
            throw new InvalidParameterException("Invalid area in key", this, key, null);
        }
    }

}
