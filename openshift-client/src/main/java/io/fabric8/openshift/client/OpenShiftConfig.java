/**
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.openshift.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.OAuthTokenProvider;
import io.fabric8.kubernetes.client.internal.readiness.Readiness;
import io.fabric8.kubernetes.client.utils.URLUtils;
import io.fabric8.kubernetes.client.utils.Utils;
import io.fabric8.openshift.client.internal.readiness.OpenShiftReadiness;
import io.sundr.builder.annotations.Buildable;
import io.sundr.builder.annotations.BuildableReference;
import okhttp3.TlsVersion;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true, allowSetters = true)
public class OpenShiftConfig extends Config {

  public static final String KUBERNETES_OAPI_VERSION_SYSTEM_PROPERTY = "kubernetes.oapi.version";
  public static final String OPENSHIFT_URL_SYSTEM_PROPERTY = "openshift.url";
  public static final String OPENSHIFT_BUILD_TIMEOUT_SYSTEM_PROPERTY = "openshift.build.timeout";

  public static final Long DEFAULT_BUILD_TIMEOUT = 5 * 60 * 1000L;

  private String oapiVersion = "v1";
  private String openShiftUrl;
  private long buildTimeout = DEFAULT_BUILD_TIMEOUT;
  private boolean openshiftApiGroupsEnabled;
  private boolean disableApiGroupCheck; //If group hasn't been explicitly set.

  //This is not meant to be used. This constructor is used only by the generated builder.
  OpenShiftConfig() {
  }

  public OpenShiftConfig(Config kubernetesConfig) {
    this(kubernetesConfig,
      getDefaultOpenShiftUrl(kubernetesConfig), getDefaultOapiVersion(kubernetesConfig), DEFAULT_BUILD_TIMEOUT
    );
  }

  public OpenShiftConfig(Config kubernetesConfig, String openShiftUrl) {
    this(kubernetesConfig,
      getDefaultOpenShiftUrl(kubernetesConfig), getDefaultOapiVersion(kubernetesConfig), DEFAULT_BUILD_TIMEOUT
    );
    this.openShiftUrl = openShiftUrl;
  }

  @Buildable(builderPackage = "io.fabric8.kubernetes.api.builder", editableEnabled = false, refs = {@BuildableReference(Config.class)})
  public OpenShiftConfig(String openShiftUrl, String oapiVersion, String masterUrl, String apiVersion, String namespace, boolean trustCerts, boolean disableHostnameVerification, String caCertFile, String caCertData, String clientCertFile, String clientCertData, String clientKeyFile, String clientKeyData, String clientKeyAlgo, String clientKeyPassphrase, String username, String password, String oauthToken, int watchReconnectInterval, int watchReconnectLimit, int connectionTimeout, int requestTimeout, long rollingTimeout, long scaleTimeout, int loggingInterval, int maxConcurrentRequests, int maxConcurrentRequestsPerHost, boolean http2Disable, String httpProxy, String httpsProxy, String[] noProxy, Map<Integer, String> errorMessages, String userAgent, TlsVersion[] tlsVersions, long websocketTimeout, long websocketPingInterval, String proxyUsername, String proxyPassword, String trustStoreFile, String trustStorePassphrase, String keyStoreFile, String keyStorePassphrase, String impersonateUsername, String[] impersonateGroups, Map<String, List<String>> impersonateExtras, OAuthTokenProvider oauthTokenProvider, Map<String,String> customHeaders, int requestRetryBackoffLimit, int requestRetryBackoffInterval, int uploadConnectionTimeout, int uploadRequestTimeout, long buildTimeout, boolean openshiftApiGroupsEnabled, boolean disableApiGroupCheck) {
    super(masterUrl, apiVersion, namespace, trustCerts, disableHostnameVerification, caCertFile, caCertData, clientCertFile, clientCertData, clientKeyFile, clientKeyData, clientKeyAlgo, clientKeyPassphrase, username, password, oauthToken, watchReconnectInterval, watchReconnectLimit, connectionTimeout, requestTimeout, rollingTimeout, scaleTimeout, loggingInterval, maxConcurrentRequests, maxConcurrentRequestsPerHost, http2Disable, httpProxy, httpsProxy, noProxy, errorMessages, userAgent, tlsVersions, websocketTimeout, websocketPingInterval, proxyUsername, proxyPassword, trustStoreFile, trustStorePassphrase, keyStoreFile, keyStorePassphrase, impersonateUsername, impersonateGroups, impersonateExtras, oauthTokenProvider, customHeaders, requestRetryBackoffLimit, requestRetryBackoffInterval, uploadConnectionTimeout, uploadRequestTimeout);
    this.oapiVersion = oapiVersion;
    this.openShiftUrl = openShiftUrl;
    this.buildTimeout = buildTimeout;
    this.openshiftApiGroupsEnabled = openshiftApiGroupsEnabled;
    this.disableApiGroupCheck = openshiftApiGroupsEnabled ? false : disableApiGroupCheck;

    if (this.openShiftUrl == null || this.openShiftUrl.isEmpty()) {
      this.openShiftUrl = URLUtils.join(getMasterUrl(), "oapi", this.oapiVersion);
    }
    if (!this.openShiftUrl.endsWith("/")) {
      this.openShiftUrl = this.openShiftUrl + "/";
    }
  }

  public OpenShiftConfig(Config kubernetesConfig, String openShiftUrl, String oapiVersion, long buildTimeout) {
    this(openShiftUrl, oapiVersion,
      kubernetesConfig.getMasterUrl(), kubernetesConfig.getApiVersion(),
      kubernetesConfig.getNamespace(), kubernetesConfig.isTrustCerts(),
      kubernetesConfig.isDisableHostnameVerification(), kubernetesConfig.getCaCertFile(),
      kubernetesConfig.getCaCertData(), kubernetesConfig.getClientCertFile(),
      kubernetesConfig.getClientCertData(), kubernetesConfig.getClientKeyFile(),
      kubernetesConfig.getClientKeyData(), kubernetesConfig.getClientKeyAlgo(), kubernetesConfig.getClientKeyPassphrase(),
      kubernetesConfig.getUsername(), kubernetesConfig.getPassword(), kubernetesConfig.getOauthToken(),
      kubernetesConfig.getWatchReconnectInterval(), kubernetesConfig.getWatchReconnectLimit(),
      kubernetesConfig.getConnectionTimeout(), kubernetesConfig.getRequestTimeout(),
      kubernetesConfig.getRollingTimeout(), kubernetesConfig.getScaleTimeout(),
      kubernetesConfig.getLoggingInterval(), kubernetesConfig.getMaxConcurrentRequests(),
      kubernetesConfig.getMaxConcurrentRequestsPerHost(), kubernetesConfig.isHttp2Disable(),
      kubernetesConfig.getHttpProxy(), kubernetesConfig.getHttpsProxy(), kubernetesConfig.getNoProxy(),
      kubernetesConfig.getErrorMessages(), kubernetesConfig.getUserAgent(),
      kubernetesConfig.getTlsVersions(), kubernetesConfig.getWebsocketTimeout(),
      kubernetesConfig.getWebsocketPingInterval(), kubernetesConfig.getProxyUsername(),
      kubernetesConfig.getProxyPassword(), kubernetesConfig.getTrustStoreFile(),
      kubernetesConfig.getTrustStorePassphrase(), kubernetesConfig.getKeyStoreFile(),
      kubernetesConfig.getKeyStorePassphrase(), kubernetesConfig.getImpersonateUsername(),
      kubernetesConfig.getImpersonateGroups(), kubernetesConfig.getImpersonateExtras(),
      kubernetesConfig.getOauthTokenProvider(), kubernetesConfig.getCustomHeaders(),
      kubernetesConfig.getRequestRetryBackoffLimit(), kubernetesConfig.getRequestRetryBackoffInterval(),
      kubernetesConfig.getUploadConnectionTimeout(), kubernetesConfig.getUploadRequestTimeout(),
      buildTimeout,
      false,
      false
      );
  }

  public static OpenShiftConfig wrap(Config config) {
    return config instanceof OpenShiftConfig ? (OpenShiftConfig) config : new OpenShiftConfig(config);
  }


  private static String getDefaultOapiVersion(Config config) {
    return Utils.getSystemPropertyOrEnvVar(KUBERNETES_OAPI_VERSION_SYSTEM_PROPERTY, config.getApiVersion());
  }

  private static String getDefaultOpenShiftUrl(Config config) {
    String openshiftUrl = Utils.getSystemPropertyOrEnvVar(OPENSHIFT_URL_SYSTEM_PROPERTY);
    if (openshiftUrl != null) {
      // The OPENSHIFT_URL environment variable may be set to the root url (i.e. without the '/oapi/version' path) in some configurations
      if (isRootURL(openshiftUrl)) {
        openshiftUrl = URLUtils.join(openshiftUrl, "oapi", getDefaultOapiVersion(config));
      }
      return openshiftUrl;
    } else {
      return URLUtils.join(config.getMasterUrl(), "oapi", getDefaultOapiVersion(config));
    }
  }

  public OpenShiftConfig withOpenshiftApiGroupsEnabled(boolean openshiftApiGroupsEnabled) {
    return new OpenShiftConfigBuilder(this).withOpenshiftApiGroupsEnabled(openshiftApiGroupsEnabled).build();
  }

  private static boolean isRootURL(String url) {
    try {
      String path = new URL(url).getPath();
      return "".equals(path) || "/".equals(path);
    } catch (MalformedURLException e) {
      return false;
    }
  }

  @JsonProperty("oapiVersion")
  public String getOapiVersion() {
    return oapiVersion;
  }

  public void setOapiVersion(String oapiVersion) {
    this.oapiVersion = oapiVersion;
  }

  @JsonProperty("openShiftUrl")
  public String getOpenShiftUrl() {
    return openShiftUrl;
  }

  public void setOpenShiftUrl(String openShiftUrl) {
    this.openShiftUrl = openShiftUrl;
  }

  @JsonProperty("buildTimeout")
  public long getBuildTimeout() {
    return buildTimeout;
  }

  public void setBuildTimeout(long buildTimeout) {
    this.buildTimeout = buildTimeout;
  }

  public boolean isDisableApiGroupCheck() {
    return disableApiGroupCheck;
  }

  public void setDisableApiGroupCheck(boolean disableApiGroupCheck) {
    this.disableApiGroupCheck = disableApiGroupCheck;
  }

  public boolean isOpenshiftApiGroupsEnabled() {
    return openshiftApiGroupsEnabled;
  }

  public void setOpenshiftApiGroupsEnabled(boolean openshiftApiGroupsEnabled) {
    this.openshiftApiGroupsEnabled = openshiftApiGroupsEnabled;
  }

  @Override
  public Readiness getReadiness() {
    return OpenShiftReadiness.getInstance();
  }
}
