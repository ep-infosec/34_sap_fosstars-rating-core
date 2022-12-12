package com.sap.oss.phosphor.fosstars.data.github;

import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.RUNS_MYPY_SCANS;
import static com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures.USES_MYPY_SCAN_CHECKS;
import static com.sap.oss.phosphor.fosstars.model.other.Utils.setOf;

import com.sap.oss.phosphor.fosstars.data.AbstractStaticScanToolsDataProvider;
import com.sap.oss.phosphor.fosstars.model.Value;
import com.sap.oss.phosphor.fosstars.model.ValueSet;
import com.sap.oss.phosphor.fosstars.model.feature.oss.OssFeatures;
import com.sap.oss.phosphor.fosstars.model.subject.oss.GitHubProject;
import com.sap.oss.phosphor.fosstars.model.value.ValueHashSet;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * The data provider gathers info about how a project uses Mypy for static analysis. In particular,
 * it tries to fill out the following features:
 * <ul>
 *   <li>{@link OssFeatures#RUNS_MYPY_SCANS}</li>
 *   <li>{@link OssFeatures#USES_MYPY_SCAN_CHECKS}</li>
 * </ul>
 */
public class MyPyDataProvider extends AbstractStaticScanToolsDataProvider {

  /**
   * A predicate to check if the any step of a GitHub action that triggers analysis with Mypy.
   */
  private static final Map<String, Predicate<String>> MATCH_PYLINT_PREDICATE_MAP = new HashMap<>();

  static {
    MATCH_PYLINT_PREDICATE_MAP.put("uses", step -> Pattern
        .compile("^.*mypy-github-action.*$", Pattern.DOTALL).matcher(step).matches());
    MATCH_PYLINT_PREDICATE_MAP.put("run",
        step -> Pattern.compile("^.*mypy .*$", Pattern.DOTALL).matcher(step).matches());
    MATCH_PYLINT_PREDICATE_MAP.putAll(preCommitHookContextMap(hook -> hook.contains("mypy")));
  }

  /**
   * Initializes a data provider.
   *
   * @param fetcher An interface to GitHub.
   */
  public MyPyDataProvider(GitHubDataFetcher fetcher) {
    super(fetcher, setOf(RUNS_MYPY_SCANS, USES_MYPY_SCAN_CHECKS));
  }

  @Override
  protected ValueSet fetchValuesFor(GitHubProject project) throws IOException {
    logger.info("Figuring out how the project uses mypy ...");

    LocalRepository repository = GitHubDataFetcher.localRepositoryFor(project);

    Visitor visitor = withVisitor();
    browse(repository, MATCH_PYLINT_PREDICATE_MAP, Collections.emptyMap(), visitor);

    Value<Boolean> runsPylint = RUNS_MYPY_SCANS.value(visitor.runCheck);
    Value<Boolean> usesPylintScanChecks = USES_MYPY_SCAN_CHECKS.value(visitor.usesCheck);

    return ValueHashSet.from(runsPylint, usesPylintScanChecks);
  }
}