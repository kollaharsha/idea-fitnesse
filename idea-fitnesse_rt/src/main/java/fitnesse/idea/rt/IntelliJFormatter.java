package fitnesse.idea.rt;

import fitnesse.reporting.Formatter;
import fitnesse.testrunner.TestsRunnerListener;
import fitnesse.testrunner.WikiTestPageUtil;
import fitnesse.testsystems.*;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.fs.FileSystemPage;
import fitnesse.wiki.fs.FileSystemPageFactory;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;

/*
 * Output:
 *
 * ##teamcity[testCount count='2']
 *
 * ##teamcity[testSuiteStarted name='FitNesseTestRunConfigurationProducerTest' locationHint='scalatest://TopOfClass:fitnesse.idea.run.FitNesseTestRunConfigurationProducerTestTestName:FitNesseTestRunConfigurationProducerTest' captureStandardOutput='true' nodeId='1' parentNodeId='0']
 *
 * ##teamcity[testStarted name='should retrieve wiki page name' locationHint='scalatest://LineInFile:fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest:FitNesseTestRunConfigurationProducerTest.scala:13TestName:should retrieve wiki page name' captureStandardOutput='true' nodeId='2' parentNodeId='1']
 *
 * ##teamcity[testFinished name='should retrieve wiki page name' duration='375'nodeId='2']
 *
 * ##teamcity[testStarted name='should retrieve nested wiki page name' locationHint='scalatest://LineInFile:fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest:FitNesseTestRunConfigurationProducerTest.scala:23TestName:should retrieve nested wiki page name' captureStandardOutput='true' nodeId='3' parentNodeId='1']
 *
 * ##teamcity[testFinished name='should retrieve nested wiki page name' duration='0'nodeId='3']
 *
 * ##teamcity[testSuiteFinished name='FitNesseTestRunConfigurationProducerTest'nodeId='1']
 *
 *
 * In case of an error, testFailed is sent instead of testFinished:
 *
 * ##teamcity[testFailed name='should retrieve nested wiki page name' message='Expected "SuitePage.TestPage[x|]", but got "SuitePage.TestPage[|]"|nScalaTestFailureLocation: fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest$$anonfun$2 at (FitNesseTestRunConfigurationProducerTest.scala:28)' details='org.scalatest.exceptions.TestFailedException: Expected "SuitePage.TestPage[x|]", but got "SuitePage.TestPage[|]"|n  at org.scalatest.Assertions$class.newAssertionFailedException(Assertions.scala:495)|n        at org.scalatest.FunSuite.newAssertionFailedException(FunSuite.scala:1555)|n    at org.scalatest.Assertions$class.assertResult(Assertions.scala:1226)|n      at org.scalatest.FunSuite.assertResult(FunSuite.scala:1555)|n   at fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest$$anonfun$2.apply$mcV$sp(FitNesseTestRunConfigurationProducerTest.scala:28)|n      at fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest$$anonfun$2.apply(FitNesseTestRunConfigurationProducerTest.scala:23)|n  at fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest$$anonfun$2.apply(FitNesseTestRunConfigurationProducerTest.scala:23)|n  at org.scalatest.Transformer$$anonfun$apply$1.apply$mcV$sp(Transformer.scala:22)|n      at org.scalatest.OutcomeOf$class.outcomeOf(OutcomeOf.scala:85)|n        at org.scalatest.OutcomeOf$.outcomeOf(OutcomeOf.scala:104)|n at org.scalatest.Transformer.apply(Transformer.scala:22)|n      at org.scalatest.Transformer.apply(Transformer.scala:20)|n      at org.scalatest.FunSuiteLike$$anon$1.apply(FunSuiteLike.scala:166)|n        at org.scalatest.Suite$class.withFixture(Suite.scala:1122)|n    at org.scalatest.FunSuite.withFixture(FunSuite.scala:1555)|n    at org.scalatest.FunSuiteLike$class.invokeWithFixture$1(FunSuiteLike.scala:163)|n    at org.scalatest.FunSuiteLike$$anonfun$runTest$1.apply(FunSuiteLike.scala:175)|n        at org.scalatest.FunSuiteLike$$anonfun$runTest$1.apply(FunSuiteLike.scala:175)|n        at org.scalatest.SuperEngine.runTestImpl(Engine.scala:306)|n at org.scalatest.FunSuiteLike$class.runTest(FunSuiteLike.scala:175)|n   at org.scalatest.FunSuite.runTest(FunSuite.scala:1555)|n        at org.scalatest.FunSuiteLike$$anonfun$runTests$1.apply(FunSuiteLike.scala:208)|n    at org.scalatest.FunSuiteLike$$anonfun$runTests$1.apply(FunSuiteLike.scala:208)|n       at org.scalatest.SuperEngine$$anonfun$traverseSubNodes$1$1.apply(Engine.scala:413)|n    at org.scalatest.SuperEngine$$anonfun$traverseSubNodes$1$1.apply(Engine.scala:401)|n at scala.collection.immutable.List.foreach(List.scala:381)|n    at org.scalatest.SuperEngine.traverseSubNodes$1(Engine.scala:401)|n     at org.scalatest.SuperEngine.org$scalatest$SuperEngine$$runTestsInBranch(Engine.scala:396)|n at org.scalatest.SuperEngine.runTestsImpl(Engine.scala:483)|n   at org.scalatest.FunSuiteLike$class.runTests(FunSuiteLike.scala:208)|n  at org.scalatest.FunSuite.runTests(FunSuite.scala:1555)|n    at org.scalatest.Suite$class.run(Suite.scala:1424)|n    at org.scalatest.FunSuite.org$scalatest$FunSuiteLike$$super$run(FunSuite.scala:1555)|n  at org.scalatest.FunSuiteLike$$anonfun$run$1.apply(FunSuiteLike.scala:212)|n at org.scalatest.FunSuiteLike$$anonfun$run$1.apply(FunSuiteLike.scala:212)|n    at org.scalatest.SuperEngine.runImpl(Engine.scala:545)|n        at org.scalatest.FunSuiteLike$class.run(FunSuiteLike.scala:212)|n    at fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest.org$scalatest$BeforeAndAfterAll$$super$run(FitNesseTestRunConfigurationProducerTest.scala:7)|n    at org.scalatest.BeforeAndAfterAll$class.liftedTree1$1(BeforeAndAfterAll.scala:257)|n        at org.scalatest.BeforeAndAfterAll$class.run(BeforeAndAfterAll.scala:256)|n     at fitnesse.idea.run.FitNesseTestRunConfigurationProducerTest.run(FitNesseTestRunConfigurationProducerTest.scala:7)|n        at org.scalatest.tools.SuiteRunner.run(SuiteRunner.scala:55)|n  at org.scalatest.tools.Runner$$anonfun$doRunRunRunDaDoRunRun$3.apply(Runner.scala:2563)|n   at org.scalatest.tools.Runner$$anonfun$doRunRunRunDaDoRunRun$3.apply(Runner.scala:2557)|n        at scala.collection.immutable.List.foreach(List.scala:381)|n    at org.scalatest.tools.Runner$.doRunRunRunDaDoRunRun(Runner.scala:2557)|n    at org.scalatest.tools.Runner$$anonfun$runOptionallyWithPassFailReporter$2.apply(Runner.scala:1044)|n   at org.scalatest.tools.Runner$$anonfun$runOptionallyWithPassFailReporter$2.apply(Runner.scala:1043)|nat org.scalatest.tools.Runner$.withClassLoaderAndDispatchReporter(Runner.scala:2722)|n  at org.scalatest.tools.Runner$.runOptionallyWithPassFailReporter(Runner.scala:1043)|n   at org.scalatest.tools.Runner$.run(Runner.scala:883)|n       at org.scalatest.tools.Runner.run(Runner.scala)|n       at org.jetbrains.plugins.scala.testingSupport.scalaTest.ScalaTestRunner.runScalaTest2(ScalaTestRunner.java:138)|n       at org.jetbrains.plugins.scala.testingSupport.scalaTest.ScalaTestRunner.main(ScalaTestRunner.java:28)|n      at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)|n        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)|n   at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)|n      at java.lang.reflect.Method.invoke(Method.java:606)|n   at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)|n' error = 'true'timestamp='2015-06-26T15:45:13.817'nodeId='3']
 *
 */
public class IntelliJFormatter implements Formatter, TestsRunnerListener {

//    private final StringBuilder outputChunks = new StringBuilder();

    private final PrintStream out;

//    private ExecutionResult executionResult;
    private ExceptionResult exceptionOccurred;

    public IntelliJFormatter() {
        this(System.out);
    }

    public IntelliJFormatter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void testSystemStarted(TestSystem testSystem) throws IOException {
        log("##teamcity[testSuiteStarted name='%s' locationHint='' captureStandardOutput='true']", testSystem.getName());
    }

    @Override
    public void testStarted(TestPage testPage) throws IOException {
        log("##teamcity[testStarted name='%s' locationHint='%s' captureStandardOutput='true']", testPage.getFullPath(), locationHint(testPage));
    }

    private String locationHint(TestPage testPage) throws IOException {
        WikiPage wikiPage = WikiTestPageUtil.getSourcePage(testPage);
        if (wikiPage instanceof FileSystemPage) {
            return "fitnesse://" + new File(((FileSystemPage) wikiPage).getFileSystemPath(), "content.txt").getCanonicalPath();
        }
        return "";
    }

    @Override
    public void testOutputChunk(String output) throws IOException {
        try {
            NodeList nodes = new Parser(new Lexer(output)).parse(null);
            print(translate(nodes));
        } catch (ParserException e) {
            log("Unparsable wiki output: %s", output);
        }
    }

    private String translate(NodeList nodes) throws IOException {
        if (nodes == null) return "";

        StringBuilder sb = new StringBuilder();

        for (Node node : nodeListIterator(nodes)) {
            if (node instanceof TableTag) {
                sb.append(translateTable(node.getChildren()));
            } else if (node instanceof Span) {
                Span span = (Span) node;
                String result = span.getAttribute("class");
                if ("pass".equals(result)) {
                    sb.append("\u001B[42m");
                } else if ("fail".equals(result)) {
                    sb.append("\u001B[41m");
                } else if ("error".equals(result)) {
                    sb.append("\u001B[43m");
                } else if ("ignore".equals(result)) {
                    sb.append("\u001B[46m");
                }
                sb.append(span.getChildrenHTML());
                sb.append("\u001B[0m ");
            } else if (node instanceof Tag && "BR".equals(((Tag) node).getTagName())) {
                sb.append("\n");
            } else if (node.getChildren() != null) {
                sb.append(translate(node.getChildren()));
            } else {
                sb.append(node.getText());
            }
        }
        return sb.toString();
    }

    private String translateTable(NodeList nodes) throws IOException {
        List<List<Cell>> table = new ArrayList<List<Cell>>();
        List<Integer> rowWidths = new ArrayList<Integer>();
        for (Node row : nodeListIterator(nodes.extractAllNodesThatMatch(new NodeClassFilter(TableRow.class)))) {
            List<Cell> tableRow = new ArrayList<Cell>();
            int rowNr = 0;
            for (Node cell : nodeListIterator(row.getChildren().extractAllNodesThatMatch(new NodeClassFilter(TableColumn.class)))) {
                Cell tableCell = new Cell(translate(cell.getChildren()), ((TableColumn) cell).getAttribute("colspan"));
                tableRow.add(tableCell);
                if (rowNr < rowWidths.size()) {
                    rowWidths.set(rowNr, Math.max(rowWidths.get(rowNr), tableCell.length));
                } else {
                    rowWidths.add(tableCell.length);
                }
                rowNr++;
            }
            table.add(tableRow);
        }

        StringBuilder sb = new StringBuilder();
        for (List<Cell> row : table) {
            int rowNr = 0;
            for (Cell cell : row) {
                sb.append(" | ").append(cell).append(padding(cell, rowWidths, rowNr));
                rowNr += cell.colspan;
            }
            sb.append(" |\n");
        }
        return sb.toString();
    }

    private char[] padding(Cell cell, List<Integer> rowWidths, int rowNr) {
        int w = 0;
        for (int i = 0; i < cell.colspan && rowNr + i < rowWidths.size(); i++) w += rowWidths.get(rowNr + i);
        w += (cell.colspan - 1) * 3; // bars
        return padding(w - cell.length);
    }

    private char[] padding(int i) {
        char[] chars = new char[i > 0 ? i : 0];
        Arrays.fill(chars, ' ');
        return chars;
    }

    private Iterable<Node> nodeListIterator(NodeList nodes) {
        final SimpleNodeIterator iter = nodes.elements();
        return new Iterable<Node>() {
            @Override
            public Iterator<Node> iterator() {
                return new Iterator<Node>() {
                    @Override public boolean hasNext() { return iter.hasMoreNodes(); }
                    @Override public Node next() { return iter.nextNode(); }
                    @Override public void remove() { }
                };
            }
        };
    }

    @Override
    public void testComplete(TestPage testPage, TestSummary summary) throws IOException {
        String fullPath = testPage.getFullPath();
        if (exceptionOccurred != null) {
            log("##teamcity[testFailed name='%s' message='%s' error='true']", fullPath, exceptionOccurred.getMessage() != null ? exceptionOccurred.getMessage().replace("'", "|'") : exceptionOccurred.toString());
            exceptionOccurred = null;
        } else if (summary.getWrong() > 0 || summary.getExceptions() > 0) {
            log("##teamcity[testFailed name='%s' message='Test failed: R:%d W:%d I:%d E:%d']", fullPath, summary.getRight(), summary.getWrong(), summary.getIgnores(), summary.getExceptions());
        } else {
            log("##teamcity[testFinished name='%s']", fullPath);
        }
    }

    @Override
    public void testAssertionVerified(Assertion assertion, TestResult testResult) {
    }

    @Override
    public void testExceptionOccurred(Assertion assertion, ExceptionResult exceptionResult) {
        if (exceptionOccurred == null) {
            exceptionOccurred = exceptionResult;
        }
    }

    @Override
    public void testSystemStopped(TestSystem testSystem, Throwable cause) {
        log("##teamcity[testSuiteFinished name='%s']", testSystem.getName());
    }


    @Override
    public void announceNumberTestsToRun(int i) {
        log("##teamcity[testCount count='%d']", i);
    }

    @Override
    public void unableToStartTestSystem(String s, Throwable throwable) throws IOException {

    }

    private void log(String s, Object... args) {
        out.println(format(s, args));
    }

    private void print(String s) throws IOException {
        out.print(s);
    }

    private static class Cell {
        private final String cellContent;
        private final int length;
        private final int colspan;

        private Cell(String cellContent, String colspan) {
            this.cellContent = cellContent;
            this.length = cellLength(cellContent);
            this.colspan = parseInt(colspan);
        }

        @Override
        public String toString() {
            return cellContent;
        }

        private static int cellLength(String tableCell) {
            return tableCell.replaceAll("\u001B.*?m", "").split("\n")[0].length();
        }

        private static int parseInt(String colspan) {
            try {
                return Integer.parseInt(colspan);
            } catch (NumberFormatException e) {
                return 1;
            }
        }
    }
}
