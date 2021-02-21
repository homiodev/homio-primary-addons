package org.touchhome.bundle.camera.workspace;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.camera.CameraEntryPoint;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.touchhome.bundle.camera.workspace.Scratch3CameraBlocks.VIDEO_STREAM;

@Log4j2
@Getter
@Component
public class Scratch3OnvifPTZBlocks extends Scratch3ExtensionBlocks implements Scratch3BaseOnvif {

    @Getter
    private final MenuBlock.ServerMenuBlock onvifCameraMenu;

    private final MenuBlock.StaticMenuBlock<PanActionType> panActionTypeMenu;
    private final MenuBlock.StaticMenuBlock<TiltActionType> tiltActionTypeMenu;
    private final MenuBlock.StaticMenuBlock<ZoomActionType> zoomActionTypeMenu;
    private final MenuBlock.StaticMenuBlock<String> presetMenu;

    private final Scratch3Block zoomReporter;
    private final Scratch3Block goToPresetReporter;
    private final Scratch3Block tiltReporter;
    private final Scratch3Block panReporter;

    private final Scratch3Block panCommand;
    private final Scratch3Block panActionCommand;
    private final Scratch3Block tiltCommand;
    private final Scratch3Block tiltActionCommand;
    private final Scratch3Block zoomCommand;
    private final Scratch3Block zoomActionCommand;
    private final Scratch3Block goToPresetCommand;

    public Scratch3OnvifPTZBlocks(EntityContext entityContext, CameraEntryPoint cameraEntryPoint) {
        super("#4f4ba6", entityContext, cameraEntryPoint, "onvifptz");

        // Menu
        this.onvifCameraMenu = MenuBlock.ofServerItems("onvifCameraMenu", OnvifCameraEntity.class);
        this.panActionTypeMenu = MenuBlock.ofStatic("panActionTypeMenu", PanActionType.class, PanActionType.Left);
        this.tiltActionTypeMenu = MenuBlock.ofStatic("tiltActionTypeMenu", TiltActionType.class, TiltActionType.Up);
        this.zoomActionTypeMenu = MenuBlock.ofStatic("zoomActionTypeMenu", ZoomActionType.class, ZoomActionType.In);
        Map<String, String> presets = IntStream.range(1, 25).boxed().collect(Collectors.toMap(String::valueOf, num -> "Preset " + num));
        this.presetMenu = MenuBlock.ofStaticList("presetMenu", presets, "1");

        this.zoomReporter = withServerOnvif(Scratch3Block.ofEvaluate(50, "get_zoom", BlockType.reporter,
                "Get zoom of [VIDEO_STREAM]", this::getZoomReporter));

        this.tiltReporter = withServerOnvif(Scratch3Block.ofEvaluate(60, "get_tilt", BlockType.reporter,
                "Get tilt of [VIDEO_STREAM]", this::getTiltReporter));

        this.panReporter = withServerOnvif(Scratch3Block.ofEvaluate(70, "get_pan", BlockType.reporter,
                "Get pan of [VIDEO_STREAM]", this::getPanReporter));

        this.goToPresetReporter = withServerOnvif(Scratch3Block.ofEvaluate(80, "get_gotopreset", BlockType.reporter,
                "Get GoToPreset of [VIDEO_STREAM]", this::getGoToPresetReporter));

        this.panCommand = withServerOnvifAndValue(Scratch3Block.ofHandler(200, "pan",
                BlockType.command, "Pan [VALUE] of [VIDEO_STREAM]", this::firePanCommand), 0);
        this.panActionCommand = withServerOnvif(Scratch3Block.ofHandler(210, "pan_cmd",
                BlockType.command, "Pan [VALUE] of [VIDEO_STREAM]", this::firePanActionCommand));
        this.panActionCommand.addArgument(VALUE, this.panActionTypeMenu);

        this.tiltCommand = withServerOnvifAndValue(Scratch3Block.ofHandler(220, "tilt",
                BlockType.command, "Tilt [VALUE] of [VIDEO_STREAM]", this::fireTiltCommand), 0);
        this.tiltActionCommand = withServerOnvif(Scratch3Block.ofHandler(230, "tilt_cmd",
                BlockType.command, "Tilt [VALUE] of [VIDEO_STREAM]", this::fireTiltActionCommand));
        this.tiltActionCommand.addArgument(VALUE, this.tiltActionTypeMenu);

        this.zoomCommand = withServerOnvifAndValue(Scratch3Block.ofHandler(240, "zoom",
                BlockType.command, "Zoom [VALUE] of [VIDEO_STREAM]", this::fireZoomCommand), 0);
        this.zoomActionCommand = withServerOnvif(Scratch3Block.ofHandler(250, "zoom_cmd",
                BlockType.command, "Zoom [VALUE] of [VIDEO_STREAM]", this::fireZoomActionCommand));
        this.zoomActionCommand.addArgument(VALUE, this.zoomActionTypeMenu);

        this.goToPresetCommand = withServerOnvif(Scratch3Block.ofHandler(260, "to_to_preset",
                BlockType.command, "Go to preset [PRESET] of [VIDEO_STREAM]", this::fireGoToPresetCommand));
        this.goToPresetCommand.addArgument("PRESET", this.presetMenu);
    }

    private int getPanReporter(WorkspaceBlock workspaceBlock) {
        return getOnvifHandler(workspaceBlock).getPan().intValue();
    }

    private int getTiltReporter(WorkspaceBlock workspaceBlock) {
        return getOnvifHandler(workspaceBlock).getTilt().intValue();
    }

    private int getGoToPresetReporter(WorkspaceBlock workspaceBlock) {
        return getOnvifHandler(workspaceBlock).getGotoPreset().intValue();
    }

    private int getZoomReporter(WorkspaceBlock workspaceBlock) {
        return getOnvifHandler(workspaceBlock).getZoom().intValue();
    }

    private void fireGoToPresetCommand(WorkspaceBlock workspaceBlock) {
        int preset = Integer.parseInt(workspaceBlock.getMenuValue("PRESET", this.presetMenu));
        getOnvifHandler(workspaceBlock).gotoPreset(preset);
    }

    private void fireZoomActionCommand(WorkspaceBlock workspaceBlock) {
        String command = workspaceBlock.getMenuValue(VALUE, this.zoomActionTypeMenu).name().toUpperCase();
        getOnvifHandler(workspaceBlock).setZoom(command);
    }

    private void fireZoomCommand(WorkspaceBlock workspaceBlock) {
        getOnvifHandler(workspaceBlock).setZoom(String.valueOf(workspaceBlock.getInputFloat(VALUE)));
    }

    private void fireTiltActionCommand(WorkspaceBlock workspaceBlock) {
        String command = workspaceBlock.getMenuValue(VALUE, this.tiltActionTypeMenu).name().toUpperCase();
        getOnvifHandler(workspaceBlock).setTilt(command);
    }

    private void fireTiltCommand(WorkspaceBlock workspaceBlock) {
        getOnvifHandler(workspaceBlock).setTilt(String.valueOf(workspaceBlock.getInputFloat(VALUE)));
    }

    private void firePanActionCommand(WorkspaceBlock workspaceBlock) {
        String command = workspaceBlock.getMenuValue(VALUE, this.panActionTypeMenu).name().toUpperCase();
        getOnvifHandler(workspaceBlock).setPan(command);
    }

    private void firePanCommand(WorkspaceBlock workspaceBlock) {
        getOnvifHandler(workspaceBlock).setPan(String.valueOf(workspaceBlock.getInputFloat(VALUE)));
    }

    private enum PanActionType {
        Left, Right, Off
    }

    private enum TiltActionType {
        Up, Down, Off
    }

    private enum ZoomActionType {
        In, Out, Off
    }
}
