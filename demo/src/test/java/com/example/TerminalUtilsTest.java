package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TerminalUtilsTest {

    @Test
    void clearScreen_doesNotThrowException() {
        assertDoesNotThrow(TerminalUtils::clearScreen);
    }

    @Test
    void sleep_withShortDuration_doesNotThrowException() {
        assertDoesNotThrow(() -> TerminalUtils.sleep(1));
    }

    @Test
    void sleep_withZeroDuration_doesNotThrowException() {
        assertDoesNotThrow(() -> TerminalUtils.sleep(0));
    }

    @Test
    void sleep_withInterruptedThread_restoresInterruptFlag() throws InterruptedException {
        // Verifica que sleep() chama Thread.currentThread().interrupt()
        // ao capturar InterruptedException, restaurando a flag de interrupção
        boolean[] interruptedAfterSleep = {false};

        Thread thread = new Thread(() -> {
            Thread.currentThread().interrupt(); // pré-interrompe a thread
            TerminalUtils.sleep(5000);          // deve retornar imediatamente
            interruptedAfterSleep[0] = Thread.currentThread().isInterrupted();
        });

        thread.start();
        thread.join(2000); // aguarda no máximo 2s

        assertFalse(thread.isAlive(), "Thread should have finished quickly after interrupt");
        assertTrue(interruptedAfterSleep[0], "Interrupt flag should be restored after sleep catches InterruptedException");
    }
}
