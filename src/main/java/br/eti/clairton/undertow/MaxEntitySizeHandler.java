package br.eti.clairton.undertow;

import static java.util.logging.Level.FINE;
import static java.util.logging.Logger.getLogger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;

public class MaxEntitySizeHandler implements HttpHandler {
  private static final Logger logger = getLogger(MaxEntitySizeHandler.class.getSimpleName());

  private final HttpHandler next;
  private final Long limit;

  public MaxEntitySizeHandler(final HttpHandler next, final Long limit) {
    this.next = next;
    this.limit = limit;
  }

  public void handleRequest(final HttpServerExchange exchange) throws Exception {
    logger.log(FINE, "Set max-entity-size undertow to {0}", limit);
    exchange.setMaxEntitySize(limit);
    next.handleRequest(exchange);
  }

  public static class Builder implements HandlerBuilder {

    public String name() {
      return "max-entity-size";
    }

    public Map<String, Class<?>> parameters() {
        return Collections.<String, Class<?>>singletonMap("limit", long.class);
    }

    public Set<String> requiredParameters() {
        return Collections.singleton("limit");
    }

    public String defaultParameter() {
        return "limit";
    }

    public HandlerWrapper build(Map<String, Object> config) {
        return new Wrapper((Long) config.get("limit"));
    }
  }

  private static class Wrapper implements HandlerWrapper {
    private final Long limit;
    
    public Wrapper(final Long limit) {
      this.limit = limit;
    }

    public HttpHandler wrap(final HttpHandler handler) {
      return new MaxEntitySizeHandler(handler, limit);
    }
  }
}
