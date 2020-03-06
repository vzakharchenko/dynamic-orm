package com.github.vzakharchenko.dynamic.orm;

import com.github.vzakharchenko.dynamic.orm.core.SpringAnnotationTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 11:00
 */
@ContextConfiguration(
        classes = {SpringAnnotationTest.class})
public abstract class AnnotationTestQueryOrm extends AbstractTestQueryOrm {

}
