package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.qModel.QTestTableVersion;
import org.testng.annotations.Test;

import java.util.HashMap;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class DiffColumnModelTest {
    @Test
    public void testDiffColumnModel(){
        DiffColumnModel diffColumnModel = new DiffColumnModel(QTestTableVersion.qTestTableVersion,new HashMap<>());
        assertNotNull(diffColumnModel.getOnlyChangedColumns());
        assertNotNull(diffColumnModel.getDiffModels());
        assertNotNull(diffColumnModel.getEffectedColumns());
        assertNull(diffColumnModel.getActualColumnDiff(QTestTableVersion.qTestTableVersion.version));
    }
}
