package org.touchhome.bundle.arduino;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.console.ConsolePluginEditor;
import org.touchhome.bundle.api.exception.UserException;
import org.touchhome.bundle.api.model.ActionResponseModel;
import org.touchhome.bundle.api.model.FileContentType;
import org.touchhome.bundle.api.model.FileModel;
import org.touchhome.bundle.api.setting.header.HeaderSettingPlugin;
import org.touchhome.bundle.api.setting.header.ShowInlineReadOnlyConsoleHeaderSetting;
import org.touchhome.bundle.arduino.setting.ConsoleArduinoUploadUsingProgrammerSetting;
import org.touchhome.bundle.arduino.setting.ConsoleArduinoVerboseSetting;
import org.touchhome.bundle.arduino.setting.header.*;
import processing.app.BaseNoGui;
import processing.app.PreferencesData;
import processing.app.Sketch;
import processing.app.SketchFile;
import processing.app.packages.UserLibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ArduinoConsolePlugin implements ConsolePluginEditor, SketchFile.TextStorage {

    private final EntityContext entityContext;
    private final ArduinoSketchService arduinoSketchService;

    private Sketch sketch;
    private FileModel content = new FileModel("sketch_default.ino", "", null, false);
    private String prevContent = "";

    public void init() {
        this.open(this.content.getName(), true);

        entityContext.setting().listenValueAndGet(ConsoleHeaderArduinoGetBoardsSetting.class, "avr-board",
                this.arduinoSketchService::selectBoard);

        entityContext.setting().listenValue(ConsoleHeaderArduinoSketchNameSetting.class, "avr-file-name", name -> {
            this.open(name, false);
            this.syncContentToUI();
        });

        entityContext.setting().listenValueAndGet(ConsoleArduinoVerboseSetting.class, "avr-verbose",
                value -> {
                    PreferencesData.setBoolean("upload.verbose", value);
                    PreferencesData.setBoolean("build.verbose", value);
                });

        entityContext.setting().listenValueAsync(ConsoleHeaderArduinoBuildSketchSetting.class, "avr-build",
                arduinoSketchService::build);

        entityContext.setting().listenValueAsync(ConsoleHeaderArduinoDeploySketchSetting.class, "avr-upload",
                () -> arduinoSketchService.upload(false));

        entityContext.setting().listenValueAsync(ConsoleArduinoUploadUsingProgrammerSetting.class, "avr-upload-using-programmer",
                () -> arduinoSketchService.upload(true));

        entityContext.setting().listenValue(ConsoleHeaderGetBoardInfoSetting.class, "avr-get-board-info",
                arduinoSketchService::getBoardInfo);

        entityContext.setting().listenValue(ConsoleHeaderArduinoPortSetting.class, "avr-select-port", serialPort -> {
            BaseNoGui.selectSerialPort(serialPort.getSystemPortName());
            BaseNoGui.onBoardOrPortChange();
        });

        entityContext.setting().listenValue(ConsoleHeaderArduinoIncludeLibrarySetting.class, "avr-include-lib", s -> {
            UserLibrary userLibrary = BaseNoGui.librariesIndexer.getInstalledLibraries().getByName(s);
            this.arduinoSketchService.importLibrary(userLibrary, this.content);
            this.syncContentToUI();
        });
    }

    @Override
    public Class<? extends HeaderSettingPlugin<?>> getFileNameHeaderAction() {
        return ConsoleHeaderArduinoSketchNameSetting.class;
    }

    @Override
    public Map<String, Class<? extends HeaderSettingPlugin<?>>> getHeaderActions() {
        Map<String, Class<? extends HeaderSettingPlugin<?>>> headerActions = new LinkedHashMap<>();
        headerActions.put("verify", ConsoleHeaderArduinoBuildSketchSetting.class);
        headerActions.put("upload", ConsoleHeaderArduinoDeploySketchSetting.class);
        headerActions.put("getBoardInfo", ConsoleHeaderGetBoardInfoSetting.class);
        headerActions.put("arduinoPort", ConsoleHeaderArduinoPortSetting.class);
        headerActions.put("boards", ConsoleHeaderArduinoGetBoardsSetting.class);
        headerActions.put("dynamicBoardsInfo", ConsoleHeaderGetBoardsDynamicSetting.class);
        headerActions.put("incl_library", ConsoleHeaderArduinoIncludeLibrarySetting.class);
        headerActions.put("console", ShowInlineReadOnlyConsoleHeaderSetting.class);
        return headerActions;
    }

    @SneakyThrows
    @Override
    public ActionResponseModel save(FileModel content) {
        if (!content.getName().endsWith(".ino")) {
            content.setName(content.getName() + ".ino");
        }
        this.content = content;
        this.updateSketch();
        return ActionResponseModel.showSuccess("SAVED");
    }

    public void updateSketch() throws IOException {
        String properParent = content.getName().substring(0, content.getName().length() - 4);
        if (this.sketch == null || !this.sketch.getName().equals(properParent)) {
            Path sketchFile = BaseNoGui.getSketchbookFolder().toPath().resolve(properParent).resolve(content.getName());
            Files.createDirectories(sketchFile.getParent());
            if (!Files.exists(sketchFile)) {
                Files.createFile(sketchFile);
                this.prevContent = ""; // uses for save content;
            }
            this.sketch = new Sketch(sketchFile.toFile());
            this.sketch.getPrimaryFile().setStorage(this);
            this.arduinoSketchService.setSketch(this.sketch);
        }
        sketch.save();
    }

    @SneakyThrows
    @Override
    public ActionResponseModel glyphClicked(String line) {
        Pattern pattern = Pattern.compile("#include( +)[<\"](.*)\\.h[>\"]");
        Matcher matcher = pattern.matcher(line);
        Set<FileModel> files = new HashSet<>();
        if (matcher.find()) {
            String includeSource = matcher.group(2);
            for (UserLibrary library : BaseNoGui.librariesIndexer.getInstalledLibraries()) {
                Path hFile = library.getSrcFolder().toPath().resolve(includeSource + ".h");
                if (Files.exists(hFile)) {
                    files.add(new FileModel(includeSource + ".h", new String(Files.readAllBytes(hFile)), FileContentType.cpp, true));

                    Path cppFile = library.getSrcFolder().toPath().resolve(includeSource + ".cpp");
                    if (Files.exists(cppFile)) {
                        files.add(new FileModel(includeSource + ".cpp", new String(Files.readAllBytes(cppFile)), FileContentType.cpp, true));
                    }
                }
            }
        }
        if (files.isEmpty()) {
            return null;
        }
        return ActionResponseModel.showFiles(files);
    }

    @Override
    public MonacoGlyphAction getGlyphAction() {
        return new MonacoGlyphAction("fas fa-external-link-square-alt", null, "#include( +)[<\"]\\w*\\.h[\">]");
    }

    public void syncContentToUI() {
        this.sendValueToConsoleEditor(entityContext);
    }

    @SneakyThrows
    private void open(String fileName, boolean createIfNotExists) {
        if (!fileName.endsWith(".ino")) {
            throw new UserException("Arduino file " + fileName + " must ends with .ino");
        }
        String properParent = fileName.substring(0, fileName.length() - 4);
        Path sketchFile = BaseNoGui.getSketchbookFolder().toPath().resolve(properParent).resolve(fileName);
        if (!Files.exists(sketchFile)) {
            if (createIfNotExists) {
                Files.createDirectories(sketchFile.getParent());
                Files.copy(BaseNoGui.getPortableFolder().toPath().resolve("default_sketch.txt"), sketchFile);
            } else {
                throw new UserException("Unable to find file: " + sketchFile.toString());
            }
        }

        this.content.setName(fileName);
        this.content.setContent(new String(Files.readAllBytes(sketchFile)));
        this.prevContent = this.content.getContent();
        this.updateSketch();
    }

    @Override
    public FileContentType getContentType() {
        return FileContentType.cpp;
    }

    @Override
    public String accept() {
        return ".ino, .cpp";
    }

    @Override
    public FileModel getValue() {
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
}
