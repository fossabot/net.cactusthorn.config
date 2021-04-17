package net.cactusthorn.config.compiler;

import javax.lang.model.element.Element;

public final class ProcessorException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    private final Element element;

    public ProcessorException(String message, Element element) {
        super(message);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }
}
