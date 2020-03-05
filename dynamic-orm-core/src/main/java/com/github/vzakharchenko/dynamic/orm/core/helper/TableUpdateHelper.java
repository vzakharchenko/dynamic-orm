package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.ArrayUtils;
import com.github.vzakharchenko.dynamic.orm.core.DMLModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 25.04.15
 * Time: 11:14
 */
public abstract class TableUpdateHelper {

    public static Map<Path<?>, Object> buildSetsValueForQModelAsMap(DMLModel staticModel,
                                                                    RelationalPath<?> qModel,
                                                                    Path... ignore) {
        try {
            Map<Path<?>, Object> map = new HashMap<>();
            List<? extends Path<?>> columns = qModel.getColumns();
            columns.stream().filter(path -> !ArrayUtils.contains(ignore, path)).forEach(path -> {
                Object value = ModelHelper.getValueFromModelByColumn(staticModel, path);
                map.put(path, value);
            });
            return map;
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }

    }


}
