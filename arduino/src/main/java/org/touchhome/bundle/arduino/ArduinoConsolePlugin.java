package org.touchhome.bundle.arduino;

import cc.arduino.contributions.packages.ContributionInstaller;
import cc.arduino.contributions.packages.ContributionsIndexer;
import cc.arduino.packages.BoardPort;
import com.fazecast.jSerialComm.SerialPort;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginEditor;
import org.touchhome.bundle.api.console.InlineLogsConsolePlugin;
import org.touchhome.bundle.api.json.ActionResponse;
import org.touchhome.bundle.api.json.Option;
import org.touchhome.bundle.api.setting.header.BundleHeaderSettingPlugin;
import org.touchhome.bundle.api.setting.header.ShowInlineReadOnlyConsoleHeaderSetting;
import org.touchhome.bundle.api.setting.header.dynamic.BundleDynamicHeaderSettingPlugin;
import org.touchhome.bundle.arduino.setting.header.*;
import processing.app.BaseNoGui;
import processing.app.Sketch;
import processing.app.SketchFile;
import processing.app.debug.TargetBoard;
import processing.app.debug.TargetPackage;
import processing.app.debug.TargetPlatform;
import processing.app.helpers.PreferencesMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ArduinoConsolePlugin implements ConsolePluginEditor, SketchFile.TextStorage {

    private final EntityContext entityContext;
    private final InlineLogsConsolePlugin inlineLogsConsolePlugin;
    private Sketch sketch;
    private EditorContent content = new EditorContent("sketch_default.ino", "");
    private String prevContent = "";

    private final ContributionInstaller contributionInstaller;
    private final ContributionsIndexer contributionsIndexer;
    private SketchController sketchController;

    public void init() {
        this.open(this.content.getName(), true);
        createBoardChangeListener();
        entityContext.setting().listenValue(ConsoleHeaderArduinoSketchNameSetting.class, "avr-file-name", name -> {
            this.open(name, false);
            this.sendContentToUI(entityContext, "arduino");
        });

        entityContext.setting().listenValueAsync(ConsoleHeaderArduinoBuildSketchSetting.class, "avr-build", () ->
                sketchController.build());

        entityContext.setting().listenValue(ConsoleHeaderArduinoPortSetting.class, "avr-select-port", serialPort -> {
            BaseNoGui.selectSerialPort(serialPort.getSystemPortName());
            BaseNoGui.onBoardOrPortChange();
        });

        entityContext.setting().listenValue(ConsoleHeaderGetBoardInfoSetting.class, "avr-get-board-info", () -> {
            SerialPort serialPort = entityContext.setting().getValue(ConsoleHeaderArduinoPortSetting.class);
            if (serialPort != null) {
                List<BoardPort> ports = BaseNoGui.getDiscoveryManager().discovery();
                BoardPort boardPort = ports.stream().filter(p -> p.getAddress().equals(serialPort.getSystemPortName())).findAny().orElse(null);
                if (boardPort != null) {
                    entityContext.ui().sendJsonMessage("ARDUINO.BOARD_INFO", boardPort);
                }
            } else {
                entityContext.ui().sendErrorMessage("ARDUINO.PORT_NOT_SELECTED");
            }
        });
    }

    @Override
    public Class<? extends BundleHeaderSettingPlugin<?>> getFileNameHeaderAction() {
        return ConsoleHeaderArduinoSketchNameSetting.class;
    }

    @Override
    public Map<String, Class<? extends BundleHeaderSettingPlugin<?>>> getHeaderActions() {
        Map<String, Class<? extends BundleHeaderSettingPlugin<?>>> headerActions = new LinkedHashMap<>();
        headerActions.put("verify", ConsoleHeaderArduinoBuildSketchSetting.class);
        headerActions.put("getBoardInfo", ConsoleHeaderGetBoardInfoSetting.class);
        headerActions.put("arduinoPort", ConsoleHeaderArduinoPortSetting.class);
        headerActions.put("boards", ConsoleHeaderArduinoGetBoardsSetting.class);
        headerActions.put("dynamicBoardsInfo", ConsoleHeaderGetBoardsDynamicSetting.class);
        headerActions.put("console", ShowInlineReadOnlyConsoleHeaderSetting.class);
        return headerActions;
    }

    @SneakyThrows
    @Override
    public ActionResponse save(EditorContent content) {
        if (!content.getName().endsWith(".ino")) {
            content.setName(content.getName() + ".ino");
        }
        String properParent = content.getName().substring(0, content.getName().length() - 4);
        if (this.sketch == null || !this.sketch.getName().equals(properParent)) {
            Path sketchDir = BaseNoGui.getSketchbookFolder().toPath().resolve(properParent);
            Files.createDirectories(sketchDir);
            Path sketchFile = sketchDir.resolve(content.getName());

            Files.write(sketchFile, content.getContent().getBytes());
            this.sketch = new Sketch(sketchFile.toFile());
            this.sketch.getPrimaryFile().setStorage(this);
            this.sketchController = new SketchController(sketch, entityContext, inlineLogsConsolePlugin);
        }
        this.content = content;
        sketch.save();

        return new ActionResponse("SAVED", ActionResponse.ResponseAction.ShowSuccessMsg);
    }

    @SneakyThrows
    private void open(String fileName, boolean createIfNotExists) {
        if (!fileName.endsWith(".ino")) {
            throw new IllegalArgumentException("Arduino file " + fileName + " must ends with .ino");
        }
        String properParent = fileName.substring(0, fileName.length() - 4);
        Path sketchFile = BaseNoGui.getSketchbookFolder().toPath().resolve(properParent).resolve(fileName);
        if (!Files.exists(sketchFile) && !createIfNotExists) {
            throw new IllegalStateException("Unable to find file: " + sketchFile.toString());
        }

        this.content.setName(fileName);
        this.content.setContent(new String(Files.readAllBytes(sketchFile)));
        if (StringUtils.isEmpty(this.prevContent)) {
            this.prevContent = this.content.getContent();
        }
        this.save(this.content);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.cpp;
    }

    @Override
    public String accept() {
        return ".ino, .cpp";
    }

    @Override
    public EditorContent getValue() {
        return content;
    }

    @Override
    public String getText() {
        return content.getContent();
    }

    @Override
    public boolean isModified() {
        return !this.prevContent.equals(content.getContent());
    }

    @Override
    public void clearModified() {
        this.prevContent = content.getContent();
    }

    private void createBoardChangeListener() {
        entityContext.setting().listenValueAndGet(ConsoleHeaderArduinoGetBoardsSetting.class, "avr-board", board -> {
            if (StringUtils.isNotEmpty(board)) {
                String[] values = board.split("~~~");
                TargetPackage targetPackage = BaseNoGui.packages.values().stream().filter(p -> p.getId().equals(values[0])).findAny().orElseThrow(() -> new RuntimeException("NO_BOARD_SELECTED"));
                TargetPlatform targetPlatform = targetPackage.platforms().stream().filter(p -> p.getId().equals(values[1])).findAny().orElseThrow(() -> new RuntimeException("NO_BOARD_SELECTED"));
                TargetBoard targetBoard = targetPlatform.getBoards().values().stream().filter(b -> b.getId().equals(values[2])).findAny().orElseThrow(() -> new RuntimeException("NO_BOARD_SELECTED"));

                BaseNoGui.selectBoard(targetBoard);
                BaseNoGui.onBoardOrPortChange();

                List<BundleDynamicHeaderSettingPlugin> dynamicSettings = new ArrayList<>();
                if (!targetBoard.getMenuIds().isEmpty()) {
                    for (Map.Entry<String, String> customMenuEntry : targetPlatform.getCustomMenus().entrySet()) {
                        if (targetBoard.getMenuIds().contains(customMenuEntry.getKey())) {
                            PreferencesMap preferencesMap = targetBoard.getMenuLabels(customMenuEntry.getKey());
                            if (!preferencesMap.isEmpty()) {
                                BundleDynamicHeaderSettingPlugin plugin = new BundleDynamicHeaderSettingPlugin() {

                                    @Override
                                    public String getKey() {
                                        return customMenuEntry.getKey();
                                    }

                                    @Override
                                    public String getIcon() {
                                        switch (customMenuEntry.getKey()) {
                                            case "cpu":
                                                return "fas fa-microchip";
                                            case "baud":
                                                return "fas fa-tachometer-alt";
                                            case "xtal":
                                            case "CrystalFreq":
                                                return "fas fa-wave-square";
                                            case "eesz":
                                                return "fas fa-flask";
                                            case "ResetMethod":
                                                return "fas fa-trash-restore-alt";
                                            case "dbg":
                                                return "fab fa-hubspot";
                                            case "lvl":
                                                return "fas fa-level-up-alt";
                                            case "ip":
                                                return "fas fa-superscript";
                                            case "vt":
                                                return "fas fa-table";
                                            case "exception":
                                                return "fas fa-exclamation-circle";
                                            case "wipe":
                                                return "fas fa-eraser";
                                            case "ssl":
                                                return "fab fa-expeditedssl";
                                        }
                                        return "fas fa-wrench";
                                    }

                                    @Override
                                    public String getTitle() {
                                        return customMenuEntry.getValue();
                                    }

                                    @Override
                                    public Class getType() {
                                        return String.class;
                                    }

                                    @Override
                                    public String getDefaultValue() {
                                        return preferencesMap.keySet().iterator().next();
                                    }

                                    @Override
                                    public SettingType getSettingType() {
                                        return SettingType.SelectBox;
                                    }

                                    @Override
                                    public Collection<Option> loadAvailableValues(EntityContext entityContext) {
                                        return Option.list(preferencesMap);
                                    }

                                    @Override
                                    public int order() {
                                        return 0;
                                    }
                                };
                                dynamicSettings.add(plugin);
                            }
                        }
                    }
                }
                entityContext.setting().updateDynamicSettings(ConsoleHeaderGetBoardsDynamicSetting.class, dynamicSettings);
            }
        });
    }
}
