package com.github.vzakharchenko.dynamic.orm;

import com.github.vzakharchenko.dynamic.orm.core.DebugSpringAnnotationTest;
import com.github.vzakharchenko.dynamic.orm.core.SpringAnnotationTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 11:00
 */
@ContextConfiguration(
        classes = {DebugSpringAnnotationTest.class})
public abstract class DebugAnnotationTestQueryOrm extends AbstractTestQueryOrm {

}
