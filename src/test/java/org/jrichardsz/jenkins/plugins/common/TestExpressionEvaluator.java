package org.jrichardsz.jenkins.plugins.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestExpressionEvaluator {

  @Test
  public void t001_detectGroovyExpression() throws Exception {
    ExpressionEvaluator evaluator = new ExpressionEvaluator();
    boolean result = evaluator.isGroovyExpression("groovy($.whoa)");
    assertTrue(result);
  }

  @Test
  public void t002_detectNonGroovyExpression() throws Exception {
    ExpressionEvaluator evaluator = new ExpressionEvaluator();
    boolean result = evaluator.isGroovyExpression("groovy(whoa)");
    assertFalse(result);
    result = evaluator.isGroovyExpression("groovy()");
    assertFalse(result);
    result = evaluator.isGroovyExpression("groovy ($.whoa)");
    assertFalse(result);
    result = evaluator.isGroovyExpression("groovy( $.whoa)");
    assertFalse(result);
  }

  @Test
  public void t003_getJsonPathFromGroovy() throws Exception {
    ExpressionEvaluator evaluator = new ExpressionEvaluator();
    String result = evaluator.getJsonPathFromGroovy("groovy($.whoa)");
    assertEquals("$.whoa", result);
  }

}
