package org.touchhome.bundle.camera.workspace;

import static org.touchhome.bundle.api.video.ffmpeg.FFMPEGFormat.RTSP_ALARMS;
import static org.touchhome.common.util.CommonUtils.addToListSafe;

import com.pivovarit.function.ThrowingBiConsumer;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.touchhome.bundle.api.EntityContext;
import org.touchhome.bundle.api.video.BaseFFMPEGVideoStreamHandler;
import org.touchhome.bundle.api.video.ffmpeg.FFMPEG;
import org.touchhome.bundle.api.workspace.WorkspaceBlock;
import org.touchhome.bundle.api.workspace.scratch.BlockType;
import org.touchhome.bundle.api.workspace.scratch.Scratch3Block;
import org.touchhome.bundle.api.workspace.scratch.Scratch3ExtensionBlocks;
import org.touchhome.bundle.camera.CameraEntryPoint;

@Log4j2
@Getter
@Component
public class Scratch3FFmpegBlocks extends Scratch3ExtensionBlocks {

  private static final int FFMPEG_WAIT_TO_START_TIMEOUT = 1000;
  private final Scratch3Block fireFFmpegCommand;
  private final Scratch3Block inputArgCommand;
  private final Scratch3Block outputArgCommand;

  public Scratch3FFmpegBlocks(EntityContext entityContext, CameraEntryPoint cameraEntryPoint) {
    super("#87B023", entityContext, cameraEntryPoint, "ffmpeg");
    setParent("media");

    this.inputArgCommand = ofValue(Scratch3Block.ofHandler(10, FFmpegApplyHandler.argsInput.name(), BlockType.command,
        "Input arg [VALUE]", this::skipHandler), "");
    this.outputArgCommand = ofValue(Scratch3Block.ofHandler(20, FFmpegApplyHandler.argsOutput.name(), BlockType.command,
        "Output arg [VALUE]", this::skipHandler), "");

    this.fireFFmpegCommand = Scratch3Block.ofHandler(30, "fire_ffmpeg",
        BlockType.command, "Run FFmpeg input [INPUT] output [OUTPUT]", this::fireFFmpegCommand);
    this.fireFFmpegCommand.addArgument("INPUT", "");
    this.fireFFmpegCommand.addArgument("OUTPUT", "");
  }

  private void skipHandler(WorkspaceBlock workspaceBlock) {
    // skip execution
  }

  private void fireFFmpegCommand(WorkspaceBlock workspaceBlock) throws InterruptedException {
    String input = workspaceBlock.getInputString("INPUT");
    String output = workspaceBlock.getInputString("OUTPUT");
    FfmpegBuilder ffmpegBuilder = new FfmpegBuilder();
    applyParentBlocks(ffmpegBuilder, workspaceBlock.getParent());

    String ffmpegLocation = BaseFFMPEGVideoStreamHandler.getFfmpegLocation();
    FFMPEG ffmpeg = new FFMPEG("FFMPEG_" + workspaceBlock.getId(),
        "FFMpeg workspace general FFMPEG", new FFMPEG.FFMPEGHandler() {
      @Override
      public String getEntityID() {
        return null;
      }

      @Override
      public void motionDetected(boolean on, String key) {

      }

      @Override
      public void audioDetected(boolean on) {

      }

      @Override
      public void ffmpegError(String error) {
        log.error("FFmpeg error: <{}>", error);

      }
    }, log, RTSP_ALARMS, ffmpegLocation, String.join(" ", ffmpegBuilder.inputArgs), input,
        String.join(" ", ffmpegBuilder.outputArgs),
        output, "", "", null);
    workspaceBlock.setState("wait ffmpeg to finish");
    try {
      ffmpeg.startConverting();
      // wait to able process start
      Thread.sleep(FFMPEG_WAIT_TO_START_TIMEOUT);
      while (!Thread.currentThread().isInterrupted() && ffmpeg.getIsAlive()) {
        Thread.sleep(1000);
      }
    } catch (InterruptedException ex) { // thread was interrupted
      log.error("Error while wait finish for ffmpeg: <{}>", ex.getMessage());
      // if someone else interrupted thread and ffmpeg not started yet - wait 1 sec to allow process to be started
      if (!ffmpeg.getIsAlive()) {
        Thread.sleep(FFMPEG_WAIT_TO_START_TIMEOUT);
      }
    }

    ffmpeg.stopConverting();
    log.info("FFmpeg has been stopped");

       /* Path ffmpegPath = entityContext.setting().getValue(CameraFFMPEGInstallPathOptions.class);
        FfmpegInputDeviceHardwareRepository repository = entityContext.getBean(FfmpegInputDeviceHardwareRepository.class);
        repository.fireFfmpeg(ffmpegPath.toString(), input, source, output, -1);*/
  }

  @SneakyThrows
  private void applyParentBlocks(FfmpegBuilder ffmpegBuilder, WorkspaceBlock parent) {
    if (parent == null || !parent.getBlockId().startsWith("ffmpeg_args")) {
      return;
    }
    applyParentBlocks(ffmpegBuilder, parent.getParent());
    Scratch3FFmpegBlocks.FFmpegApplyHandler.valueOf(parent.getOpcode()).applyFn.accept(parent, ffmpegBuilder);
  }

  private Scratch3Block ofValue(Scratch3Block scratch3Block, String value) {
    scratch3Block.addArgument(VALUE, value);
    return scratch3Block;
  }

  @AllArgsConstructor
  private enum FFmpegApplyHandler {
    argsInput((workspaceBlock, builder) -> {
      addToListSafe(builder.inputArgs, workspaceBlock.getInputString(VALUE).trim());
    }),
    argsOutput((workspaceBlock, builder) -> {
      addToListSafe(builder.outputArgs, workspaceBlock.getInputString(VALUE).trim());
    });

    private final ThrowingBiConsumer<WorkspaceBlock, FfmpegBuilder, Exception> applyFn;
  }

  private static class FfmpegBuilder {

    private List<String> inputArgs = new ArrayList<>();
    private List<String> outputArgs = new ArrayList<>();
  }
}
