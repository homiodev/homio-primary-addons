package org.touchhome.bundle.camera.workspace;

import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.MenuBlock;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.camera.entity.OnvifCameraEntity;
import org.touchhome.bundle.camera.handler.impl.OnvifCameraHandler;

import static org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks.VALUE;
import static org.touchhome.bundle.camera.workspace.Scratch3CameraBlocks.VIDEO_STREAM;

public interface Scratch3BaseOnvif {
    MenuBlock.ServerMenuBlock getOnvifCameraMenu();

    default Scratch3Block withServerOnvif(Scratch3Block scratch3Block) {
        scratch3Block.addArgument(VIDEO_STREAM, getOnvifCameraMenu());
        return scratch3Block;
    }

    default Scratch3Block withServerOnvifAndValue(Scratch3Block scratch3Block, int value) {
        scratch3Block.addArgument(VALUE, value);
        return withServerOnvif(scratch3Block);
    }

    default OnvifCameraHandler getOnvifHandler(WorkspaceBlock workspaceBlock) {
        return getOnvifEntity(workspaceBlock).getCameraHandler();
    }

    default OnvifCameraEntity getOnvifEntity(WorkspaceBlock workspaceBlock) {
        return workspaceBlock.getMenuValueEntityRequired(VIDEO_STREAM, getOnvifCameraMenu());
    }
}
