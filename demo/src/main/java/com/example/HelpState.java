package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HelpState {
    private int hintCount;
    private int fiftyCount;
    private int skipCount;

    public HelpState() {
        this.hintCount = 1;
        this.fiftyCount = 1;
        this.skipCount = 1;
    }

    public boolean isHintAvailable() {
        return hintCount > 0;
    }

    public boolean isFiftyAvailable() {
        return fiftyCount > 0;
    }

    public boolean isSkipAvailable() {
        return skipCount > 0;
    }

    public boolean noHelpsLeft() {
        return !isHintAvailable() && !isFiftyAvailable() && !isSkipAvailable();
    }

    public int getHintCount() {
        return hintCount;
    }

    public int getFiftyCount() {
        return fiftyCount;
    }

    public int getSkipCount() {
        return skipCount;
    }

    public void useHint() {
        hintCount = Math.max(0, hintCount - 1);
    }

    public void useFifty() {
        fiftyCount = Math.max(0, fiftyCount - 1);
    }

    public void useSkip() {
        skipCount = Math.max(0, skipCount - 1);
    }

    public String grantRandomHelp() {
        int pick = ThreadLocalRandom.current().nextInt(3);
        switch (pick) {
            case 0 -> {
                hintCount++;
                return "Dica";
            }
            case 1 -> {
                fiftyCount++;
                return "50/50";
            }
            default -> {
                skipCount++;
                return "Pular";
            }
        }
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

