package org.touchhome.bundle.cloud;

public interface CloudProvider {

    String getStatus();

    void start();

    void stop();
}
