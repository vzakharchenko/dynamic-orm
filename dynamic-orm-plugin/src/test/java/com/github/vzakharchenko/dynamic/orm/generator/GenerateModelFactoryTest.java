package com.github.vzakharchenko.dynamic.orm.generator;

import com.github.vzakharchenko.dynamic.orm.generator.qModel.QBotable;
import org.testng.annotations.Test;

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
