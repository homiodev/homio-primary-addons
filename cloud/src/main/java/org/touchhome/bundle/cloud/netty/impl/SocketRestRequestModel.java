package org.touchhome.bundle.cloud.netty.impl;

import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@NoArgsConstructor
class SocketRestRequestModel extends SocketBaseModel {

  private int requestId;
  private HttpMethod httpMethod;
  private String path;
  private HttpContentType contentType;
  private Map<String, String[]> parameters;
  private int contentLength;
  private byte[] request;
}
