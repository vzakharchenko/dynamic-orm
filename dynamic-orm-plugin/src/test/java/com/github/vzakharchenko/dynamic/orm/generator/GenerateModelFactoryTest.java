package com.github.vzakharchenko.dynamic.orm.generator;

import org.testng.annotations.Test;
import com.github.vzakharchenko.dynamic.orm.generator.qModel.QBotable;

/**
 *
 */
public class GenerateModelFactoryTest {

    @Test(enabled = true)
    public void testGenerateModel() throws Exception {
        GenerateModelFactory factory = new GenerateModelFactory();
        factory.generate(QBotable.class, "BoTable", "com.github.vzakharchenko.dynamic.orm.generator.model", "target");
    }
}
