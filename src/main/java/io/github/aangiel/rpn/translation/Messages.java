package io.github.aangiel.rpn.translation;

import io.github.aangiel.rpn.translation.interfaces.MessageTranslator;

public enum Messages implements MessageTranslator {
    EMPTY_EQUATION, LEFT_ON_STACK, LACK_OF_ARGUMENTS, BAD_ITEM, UNSUPPORTED_TYPE;

    @Override
    public String getName() {
        return name();
    }


}
