package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HelpStateTest {

    @Test
    void counters_startAtOne() {
        HelpState helps = new HelpState();
        assertEquals(1, helps.getHintCount());
        assertEquals(1, helps.getFiftyCount());
        assertEquals(1, helps.getSkipCount());
    }

    @Test
    void afterUsingEachHelp_counterBecomesZero() {
        HelpState helps = new HelpState();
        helps.useHint();
        helps.useFifty();
        helps.useSkip();
        assertEquals(0, helps.getHintCount());
        assertEquals(0, helps.getFiftyCount());
        assertEquals(0, helps.getSkipCount());
    }

    @Test
    void usingHelpAtZero_doesNotGoNegative() {
        HelpState helps = new HelpState();
        helps.useHint(); // 1 -> 0
        helps.useHint(); // stays 0
        assertEquals(0, helps.getHintCount());
    }

    @Test
    void noHelpsLeft_and_formatAvailableHelps_whenAllZero() {
        HelpState helps = new HelpState();
        helps.useHint();
        helps.useFifty();
        helps.useSkip();
        assertTrue(helps.noHelpsLeft());
        assertEquals("nenhuma", helps.formatAvailableHelps());
    }

    @Test
    void grantRandomHelp_incrementsExactlyOneCounterByOne() {
        HelpState helps = new HelpState();

        int hintBefore = helps.getHintCount();
        int fiftyBefore = helps.getFiftyCount();
        int skipBefore = helps.getSkipCount();

        String granted = helps.grantRandomHelp();

        int hintAfter = helps.getHintCount();
        int fiftyAfter = helps.getFiftyCount();
        int skipAfter = helps.getSkipCount();

        int hintDelta = hintAfter - hintBefore;
        int fiftyDelta = fiftyAfter - fiftyBefore;
        int skipDelta = skipAfter - skipBefore;

        assertEquals(hintDelta + fiftyDelta + skipDelta, 1);
        assertTrue(hintDelta == 0 || hintDelta == 1);
        assertTrue(fiftyDelta == 0 || fiftyDelta == 1);
        assertTrue(skipDelta == 0 || skipDelta == 1);

        // If returned value matches, it should be the counter that increased.
        switch (granted) {
            case "Dica" -> assertEquals(1, hintDelta);
            case "50/50" -> assertEquals(1, fiftyDelta);
            case "Pular" -> assertEquals(1, skipDelta);
            default -> fail("Invalid help name: " + granted);
        }
    }

    @Test
    void grantRandomHelp_returnsValidHelpName() {
        HelpState helps = new HelpState();
        for (int i = 0; i < 50; i++) {
            String granted = helps.grantRandomHelp();
            assertTrue(granted.equals("Dica") || granted.equals("50/50") || granted.equals("Pular"));
        }
    }
}

