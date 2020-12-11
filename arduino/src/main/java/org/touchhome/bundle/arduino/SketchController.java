package org.touchhome.bundle.arduino;

import cc.arduino.Compiler;
import com.pivovarit.function.ThrowingSupplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.InlineLogsConsolePlugin;
import processing.app.Sketch;

@RequiredArgsConstructor
public class SketchController {
    private final Sketch sketch;
    private final EntityContext entityContext;
    private final InlineLogsConsolePlugin inlineLogsConsolePlugin;

    @SneakyThrows
    public void build() {
        String progressKey = "avr-build";

        inlineLogsConsolePlugin.consoleLogUsingStdout((ThrowingSupplier<Void, Exception>) () -> {
            entityContext.ui().progress(progressKey, 20, "Compiling sketch");
            new Compiler(sketch).build(value -> entityContext.ui().progress(progressKey, value, "Compiling sketch"), true);
            return null;
        }, () -> entityContext.ui().progressDone(progressKey));
    }
}
