package org.touchhome.bundle.camera.ffmpeg;

import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.touchhome.bundle.api.util.TouchHomeUtils;
import org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.FFmpegFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.touchhome.bundle.api.util.TouchHomeUtils.addToListSafe;
import static org.touchhome.bundle.camera.onvif.util.IpCameraBindingConstants.CHANNEL_FFMPEG_MOTION_ALARM;

/**
 * Responsible for handling multiple ffmpeg conversions which are used for many tasks
 */
public class Ffmpeg {

    private final FFmpegHandler handler;
    private final Logger log;
    private final Runnable destroyListener;
    private Process process = null;
    private FFmpegFormat format;
    @Getter
    private List<String> commandArrayList = new ArrayList<>();
    private IpCameraFfmpegThread ipCameraFfmpegThread;
    private int keepAlive = 8;

    public Ffmpeg(FFmpegHandler handler, Logger log, FFmpegFormat format, String ffmpegLocation, String inputArguments,
                  String input, String outArguments, String output, String username, String password, Runnable destroyListener) {
        this.log = log;
        this.format = format;
        this.destroyListener = destroyListener;
        this.handler = handler;
        this.ipCameraFfmpegThread = new IpCameraFfmpegThread();
        inputArguments = inputArguments.trim();
        List<String> builder = new ArrayList<>();
        addToListSafe(builder, inputArguments.trim());
        if (!input.startsWith("-i")) {
            builder.add("-i");
        }
        // Input can be snapshots not just rtsp or http
        if (!password.isEmpty() && !input.contains("@") && input.contains("rtsp")) {
            String credentials = username + ":" + password + "@";
            // will not work for https: but currently binding does not use https
            builder.add(input.substring(0, 7) + credentials + input.substring(7));
        } else {
            builder.add(input);
        }
        builder.add(outArguments.trim());
        builder.add(output.trim());

        Collections.addAll(commandArrayList, String.join(" ", builder).split("\\s+"));
        // ffmpegLocation may have a space in its folder
        commandArrayList.add(0, ffmpegLocation);
        log.info("\n\nGenerated ffmpeg command for: {}.\n{}\n\n", format, String.join(" ", commandArrayList));
    }

    public void setKeepAlive(int seconds) {
        // We poll every 8 seconds due to mjpeg stream requirement.
        if (keepAlive == -1 && seconds > 1) {
            return;// When set to -1 this will not auto turn off stream.
        }
        keepAlive = seconds;
    }

    public boolean stopProcessIfNoKeepAlive() {
        if (keepAlive == 1) {
            stopConverting();
        } else if (keepAlive <= -1 && !getIsAlive()) {
            return startConverting();
        }
        if (keepAlive > 0) {
            keepAlive--;
        }
        return false;
    }

    public synchronized boolean startConverting() {
        if (!ipCameraFfmpegThread.isAlive()) {
            ipCameraFfmpegThread = new IpCameraFfmpegThread();
            ipCameraFfmpegThread.start();
            return true;
        }
        if (keepAlive != -1) {
            keepAlive = 8;
        }
        return false;
    }

    public boolean getIsAlive() {
        return process != null && process.isAlive();
    }

    public void stopConverting() {
        if (ipCameraFfmpegThread.isAlive()) {
            log.debug("Stopping ffmpeg {} now when keepalive is:{}", format, keepAlive);
            if (process != null) {
                process.destroyForcibly();
            }
            if (destroyListener != null) {
                destroyListener.run();
            }
        }
    }

    public interface FFmpegHandler {

        String getEntityID();

        void motionDetected(boolean on, String key);

        void audioDetected(boolean on);

        void ffmpegError(String error);
    }

    private class IpCameraFfmpegThread extends Thread {
        public int countOfMotions;

        IpCameraFfmpegThread() {
            setDaemon(true);
            setName("CameraThread_" + format + "_" + handler.getEntityID());
        }

        @Override
        public void run() {
            try {
                process = Runtime.getRuntime().exec(commandArrayList.toArray(new String[0]));
                Process localProcess = process;
                if (localProcess != null) {
                    InputStream errorStream = localProcess.getErrorStream();
                    InputStreamReader errorStreamReader = new InputStreamReader(errorStream);
                    BufferedReader bufferedReader = new BufferedReader(errorStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (format.equals(FFmpegFormat.RTSP_ALARMS)) {
                            log.info("{}", line);
                            if (line.contains("lavfi.")) {
                                if (countOfMotions == 4) {
                                    handler.motionDetected(true, CHANNEL_FFMPEG_MOTION_ALARM);
                                } else {
                                    countOfMotions++;
                                }
                            } else if (line.contains("speed=")) {
                                if (countOfMotions > 0) {
                                    countOfMotions--;
                                    countOfMotions--;
                                    if (countOfMotions <= 0) {
                                        handler.motionDetected(false, CHANNEL_FFMPEG_MOTION_ALARM);
                                    }
                                }
                            } else if (line.contains("silence_start")) {
                                handler.audioDetected(false);
                            } else if (line.contains("silence_end")) {
                                handler.audioDetected(true);
                            }
                        } else {
                            log.info("{}", line);
                        }
                        if (line.contains("No such file or directory")) {
                            handler.ffmpegError(line);
                        }
                    }
                }
            } catch (IOException ex) {
                log.warn("An error occurred trying to process the messages from FFmpeg.");
                handler.ffmpegError(TouchHomeUtils.getErrorMessage(ex));
            }
        }
    }
}
