package io.sentry.spring.boot;

import com.jakewharton.nopen.annotation.Open;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration for Sentry integration. */
@ConfigurationProperties("sentry")
@Open
public class SentryProperties extends SentryOptions {

  /** Weather to use Git commit id as a release. */
  private boolean useGitCommitIdAsRelease = true;

  /**
   * Turns tracing on or off. Default is disabled.
   *
   * @deprecated use {{@link SentryProperties#setTracesSampleRate(Double)} } or create {@link
   *     io.sentry.SentryOptions.TracesSamplerCallback} bean instead
   */
  @Deprecated private boolean enableTracing;

  /** Report all or only uncaught web exceptions. */
  private int exceptionResolverOrder = 1;

  /** Logging framework integration properties. */
  private @NotNull Logging logging = new Logging();

  public boolean isUseGitCommitIdAsRelease() {
    return useGitCommitIdAsRelease;
  }

  public void setUseGitCommitIdAsRelease(boolean useGitCommitIdAsRelease) {
    this.useGitCommitIdAsRelease = useGitCommitIdAsRelease;
  }

  @Deprecated
  public boolean isEnableTracing() {
    return enableTracing;
  }

  @Deprecated
  public void setEnableTracing(boolean enableTracing) {
    this.enableTracing = enableTracing;
  }

  /**
   * Returns the order used for Spring SentryExceptionResolver, which determines whether all web
   * exceptions are reported, or only uncaught exceptions.
   *
   * @return order to use for Spring SentryExceptionResolver
   */
  public int getExceptionResolverOrder() {
    return exceptionResolverOrder;
  }

  /**
   * Sets the order to use for Spring SentryExceptionResolver, which determines whether all web
   * exceptions are reported, or only uncaught exceptions.
   *
   * @param exceptionResolverOrder order to use for Spring SentryExceptionResolver
   */
  public void setExceptionResolverOrder(int exceptionResolverOrder) {
    this.exceptionResolverOrder = exceptionResolverOrder;
  }

  public Logging getLogging() {
    return logging;
  }

  public void setLogging(@NotNull Logging logging) {
    this.logging = logging;
  }

  @Open
  public static class Logging {
    /** Enable/Disable logging auto-configuration. */
    private boolean enabled = true;

    /** Minimum logging level for recording breadcrumb. */
    private @Nullable Level minimumBreadcrumbLevel;

    /** Minimum logging level for recording event. */
    private @Nullable Level minimumEventLevel;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public @Nullable Level getMinimumBreadcrumbLevel() {
      return minimumBreadcrumbLevel;
    }

    public void setMinimumBreadcrumbLevel(@Nullable Level minimumBreadcrumbLevel) {
      this.minimumBreadcrumbLevel = minimumBreadcrumbLevel;
    }

    public @Nullable Level getMinimumEventLevel() {
      return minimumEventLevel;
    }

    public void setMinimumEventLevel(@Nullable Level minimumEventLevel) {
      this.minimumEventLevel = minimumEventLevel;
    }
  }
}
