package com.example;

import java.util.ArrayList;
import java.util.List;

public class HelpState {
    private boolean hintUsed;
    private boolean fiftyUsed;
    private boolean skipUsed;

    public boolean isHintAvailable() {
        return !hintUsed;
    }

    public boolean isFiftyAvailable() {
        return !fiftyUsed;
    }

    public boolean isSkipAvailable() {
        return !skipUsed;
    }

    public boolean noHelpsLeft() {
        return !isHintAvailable() && !isFiftyAvailable() && !isSkipAvailable();
    }

    public void useHint() {
        hintUsed = true;
    }

    public void useFifty() {
        fiftyUsed = true;
    }

    public void useSkip() {
        skipUsed = true;
    }

    public void resetHint() {
        hintUsed = false;
    }

    public String formatAvailableHelps() {
        List<String> available = new ArrayList<>();
        if (isHintAvailable()) {
            available.add("Dica");
        }
        if (isFiftyAvailable()) {
            available.add("50/50");
        }
        if (isSkipAvailable()) {
            available.add("Pular");
        }
        if (available.isEmpty()) {
            return "nenhuma";
        }
        return String.join(", ", available);
    }
}
