package org.vrspace.server.obj;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.vrspace.server.core.Scene;
import org.vrspace.server.dto.SceneProperties;
import org.vrspace.server.dto.VREvent;
import org.vrspace.server.types.Owned;
import org.vrspace.server.types.Private;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = false)
@NodeEntity
@Owned
@Slf4j
public class Client extends VRObject {

  @Index(unique = true)
  private String name;
  @Transient
  transient private Point leftArmPos;
  @Transient
  transient private Point rightArmPos;
  @Transient
  transient private Quaternion leftArmRot;
  @Transient
  transient private Quaternion rightArmRot;

  // CHECKME: this needs to get refactored eventually
  @JsonIgnore
  private Set<VRObject> owned;

  @Private
  @Transient
  transient private SceneProperties sceneProperties;

  // CHECKME OpenVidu token; should that be Map tokens?
  @Private
  @Transient
  transient private String token;

  @JsonIgnore
  @Transient
  transient private WebSocketSession session;
  @JsonIgnore
  @Transient
  transient private Scene scene;
  @JsonIgnore
  @Transient
  transient private ObjectMapper mapper;
  @JsonIgnore
  @Transient
  transient private boolean guest;

  public Client() {
    super();
    this.setActive(true);
  }

  // used in tests
  public Client(Long id) {
    this();
    this.setId(id);
  }

  // used in tests
  public Client(String name) {
    this();
    this.name = name;
  }

  public Client(WebSocketSession session) {
    this();
    this.session = session;
  }

  @Override
  public void processEvent(VREvent event) {
    // TODO optimize this:
    // called like that, every client performs serialization for itself
    // event should already contain serialized message
    // dispatcher can do that
    sendMessage(event);
    // stop listening to inactive objects (disconnected clients)
    if (!event.getSource().isActive()) {
      event.getSource().removeListener(this);
      // CHECKME: do we wish to force scene refresh?
      if (scene != null) {
        // some clients (e.g. EventRecorder) may not have scene
        scene.setDirty();
      }
    }
  }

  public void sendMessage(Object obj) {
    try {
      String json = mapper.writeValueAsString(obj);
      log.debug(getObjectId() + " Received " + json);
      // TODO this is not thread-safe
      synchronized (this) {
        session.sendMessage(new TextMessage(json));
      }
    } catch (IOException e) {
      log.error("Can't send message " + obj, e);
    } catch (IllegalStateException e) {
      log.error("Can't send message " + obj, e);
    }

  }

  public void addOwned(VRObject... objects) {
    if (owned == null) {
      owned = new HashSet<VRObject>();
    }
    for (VRObject obj : objects) {
      owned.add(obj);
    }
  }

  public void removeOwned(VRObject... objects) {
    if (owned != null) {
      for (VRObject obj : objects) {
        owned.remove(obj);
      }
    }
  }

  public boolean isOwner(VRObject obj) {
    return this.equals(obj) || owned != null && obj != null && owned.contains(obj);
  }
}
