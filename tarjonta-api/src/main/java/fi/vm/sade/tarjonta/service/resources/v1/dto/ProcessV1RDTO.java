package fi.vm.sade.tarjonta.service.resources.v1.dto;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProcessV1RDTO implements Serializable {

  private String id;
  private String process;
  private double state = 0.0;
  private final long started = System.currentTimeMillis();

  private String messageKey = "progress.message.default";
  private Map<String, String> parameters = new HashMap<>();

  InetAddress ip;
  String session;
  String userAgent;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getProcess() {
    return process;
  }

  public void setProcess(String process) {
    this.process = process;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public void setMessageKey(String messageKey) {
    this.messageKey = messageKey;
  }

  public double getState() {
    return state;
  }

  public void setState(double state) {
    this.state = state;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public long getStarted() {
    return started;
  }

  private ProcessV1RDTO() {}

  public InetAddress getIp() {
    return ip;
  }

  public void setIp(InetAddress ip) {
    this.ip = ip;
  }

  public String getSession() {
    return session;
  }

  public void setSession(String session) {
    this.session = session;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public static ProcessV1RDTO generate() {
    ProcessV1RDTO p = new ProcessV1RDTO();
    // Generate new id for the process
    p.setId("" + new Random().nextLong());
    return p;
  }
}
