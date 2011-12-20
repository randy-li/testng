package org.testng.reporters.jq;

import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.collections.ListMultiMap;
import org.testng.collections.Lists;
import org.testng.collections.Maps;

import java.util.List;
import java.util.Map;

public class Model {
  private ListMultiMap<ISuite, ITestResult> m_model = Maps.newListMultiMap();
  private List<ISuite> m_suites = null;
  private Map<ISuite, String> m_suiteTags = Maps.newHashMap();
  private Map<String, String> m_testTags = Maps.newHashMap();
  private Map<ITestResult, String> m_testResultMap = Maps.newHashMap();
  private ListMultiMap<Class, ITestResult> m_failedResultsByClass = Maps.newListMultiMap();

  public Model(List<ISuite> suites) {
    m_suites = suites;
    init();
  }

  private void init() {
    int counter = 0;
    int testCounter = 0;
    for (ISuite suite : m_suites) {
      m_suiteTags.put(suite, "suite-" + counter++);
      List<ITestResult> passed = Lists.newArrayList();
      List<ITestResult> failed = Lists.newArrayList();
      List<ITestResult> skipped = Lists.newArrayList();
      for (ISuiteResult sr : suite.getResults().values()) {
        ITestContext context = sr.getTestContext();
        m_testTags.put(context.getName(), "test-" + testCounter++);
        failed.addAll(context.getFailedTests().getAllResults());
        skipped.addAll(context.getSkippedTests().getAllResults());
        passed.addAll(context.getPassedTests().getAllResults());
        IResultMap[] map = new IResultMap[] {
            context.getFailedTests(),
            context.getSkippedTests(),
            context.getPassedTests()
        };
        for (IResultMap m : map) {
          for (ITestResult tr : m.getAllResults()) {
            m_testResultMap.put(tr, getTestResultName(tr));
          }
        }
      }
      for (ITestResult tr : failed) {
        m_failedResultsByClass.put(tr.getTestClass().getRealClass(), tr);
      }
      m_model.putAll(suite, failed);
      m_model.putAll(suite, skipped);
      m_model.putAll(suite, passed);
    }
    System.out.println("Model size:" + m_model);
  }

  public ListMultiMap<Class, ITestResult> getFailedResultsByClass() {
    return m_failedResultsByClass;
  }

  public String getTag(ISuite s) {
    return m_suiteTags.get(s);
  }

  public String getTag(ITestResult tr) {
    return m_testResultMap.get(tr);
  }

  public List<ITestResult> getTestResults(ISuite suite) {
    return m_model.get(suite);
   }

  public static String getTestResultName(ITestResult tr) {
    return tr.getMethod().getMethodName();
  }

}