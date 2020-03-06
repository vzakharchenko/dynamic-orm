package com.github.vzakharchenko.dynamic.orm;

import org.springframework.test.context.ContextConfiguration;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 11:00
 */
@ContextConfiguration(
        locations = {"classpath:defaultDynamicQueryBeans.xml", "classpath:MSSQLTestQueryOrm.xml",
                "classpath:test-spring-dao-context.xml"})
public abstract class MSSQLTestQueryOrm extends AbstractTestQueryOrm {

}
