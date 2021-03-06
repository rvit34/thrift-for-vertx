/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransportFactory;
import org.vertx.java.core.ServerSSLSupport;

/**
 * Generic interface for a Thrift server.
 *
 */
public abstract class TServer {

  @SuppressWarnings("unchecked")
  public static abstract class AbstractServerArgs<T extends AbstractServerArgs<T>> {
    TProcessorFactory processorFactory;
    TTransportFactory inputTransportFactory = new TTransportFactory();
    TTransportFactory outputTransportFactory = new TTransportFactory();
    TProtocolFactory inputProtocolFactory = new TBinaryProtocol.Factory();
    TProtocolFactory outputProtocolFactory = new TBinaryProtocol.Factory();

    public AbstractServerArgs() {
    }

    public T processorFactory(TProcessorFactory factory) {
      this.processorFactory = factory;
      return (T) this;
    }

    public T processor(TProcessor processor) {
      this.processorFactory = new TProcessorFactory(processor);
      return (T) this;
    }

    public T transportFactory(TTransportFactory factory) {
      this.inputTransportFactory = factory;
      this.outputTransportFactory = factory;
      return (T) this;
    }

    public T inputTransportFactory(TTransportFactory factory) {
      this.inputTransportFactory = factory;
      return (T) this;
    }

    public T outputTransportFactory(TTransportFactory factory) {
      this.outputTransportFactory = factory;
      return (T) this;
    }

    public T protocolFactory(TProtocolFactory factory) {
      this.inputProtocolFactory = factory;
      this.outputProtocolFactory = factory;
      return (T) this;
    }

    public T inputProtocolFactory(TProtocolFactory factory) {
      this.inputProtocolFactory = factory;
      return (T) this;
    }

    public T outputProtocolFactory(TProtocolFactory factory) {
      this.outputProtocolFactory = factory;
      return (T) this;
    }
  }

  @SuppressWarnings("unchecked")
  public static abstract class AbstractServerArgsWithSSLSupport<T extends AbstractServerArgsWithSSLSupport<T>>
      extends AbstractServerArgs<AbstractServerArgsWithSSLSupport<T>> {
    boolean ssl = false;
    String keyStorePath;
    String keyStorePassword;
    String trustStorePath;
    String trustStorePassword;
    boolean clientAuthRequired = false;
    
    public AbstractServerArgsWithSSLSupport() { }
    
    @SuppressWarnings("rawtypes")
    public void configureSSL(ServerSSLSupport server) {
      if (!ssl)
        return;
      server.setSSL(true);
      server.setKeyStorePath(keyStorePath);
      server.setKeyStorePassword(keyStorePassword);
      if (clientAuthRequired) {
        server.setTrustStorePath(trustStorePath);
        server.setTrustStorePassword(trustStorePassword);
        server.setClientAuthRequired(true);
      }
    }
    
    public T setSSL(boolean ssl) {
      this.ssl = ssl;
      return (T) this;
    }

    public T setKeyStorePath(String keyStorePath) {
      this.keyStorePath = keyStorePath;
      return (T) this;
    }

    public T setKeyStorePassword(String keyStorePassword) {
      this.keyStorePassword = keyStorePassword;
      return (T) this;
    }

    public T setTrustStorePath(String trustStorePath) {
      this.trustStorePath = trustStorePath;
      return (T) this;
    }

    public T setTrustStorePassword(String trustStorePassword) {
      this.trustStorePassword = trustStorePassword;
      return (T) this;
    }

    public T setClientAuthRequired(boolean clientAuthRequired) {
      this.clientAuthRequired = clientAuthRequired;
      return (T) this;
    }
  }

  /**
   * Core processor
   */
  protected TProcessorFactory processorFactory_;

  /**
   * Input Transport Factory
   */
  protected TTransportFactory inputTransportFactory_;

  /**
   * Output Transport Factory
   */
  protected TTransportFactory outputTransportFactory_;

  /**
   * Input Protocol Factory
   */
  protected TProtocolFactory inputProtocolFactory_;

  /**
   * Output Protocol Factory
   */
  protected TProtocolFactory outputProtocolFactory_;

  private boolean isServing;

  protected TServerEventHandler eventHandler_;

  protected TServer(AbstractServerArgs<?> args) {
    processorFactory_ = args.processorFactory;
    inputTransportFactory_ = args.inputTransportFactory;
    outputTransportFactory_ = args.outputTransportFactory;
    inputProtocolFactory_ = args.inputProtocolFactory;
    outputProtocolFactory_ = args.outputProtocolFactory;
  }

  /**
   * The run method fires up the server and gets things going.
   */
  public abstract void serve();

  /**
   * Stop the server. This is optional on a per-implementation basis. Not
   * all servers are required to be cleanly stoppable.
   */
  public void stop() {}

  public boolean isServing() {
    return isServing;
  }

  protected void setServing(boolean serving) {
    isServing = serving;
  }

  public void setServerEventHandler(TServerEventHandler eventHandler) {
    eventHandler_ = eventHandler;
  }

  public TServerEventHandler getEventHandler() {
    return eventHandler_;
  }
}
