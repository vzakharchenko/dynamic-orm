package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;

import java.util.Map;

/**
 *
 */
public abstract class MapModelFactory {

    public static MapModel buildMapModel(
            RelationalPath<?> qTable, Map<Path<?>, Object> diffModels) {
        return new MapModel(qTable, diffModels);
    }

    public static MapModel buildMapModel(
            RelationalPath<?> qTable, DMLModel model) {
        return new MapModel(qTable, model);
    }

//    public static MapModel mergeMapModels(
//            RelationalPath<?> qTable, MapModel origin, MapModel merge) {
//        Map<Path<?>, Object> mapModels = new HashMap<>();
//        if (origin != null) {
//            mapModels.putAll(origin.getDiffModel());
//        }
//        Assert.notNull(merge);
//        mapModels.putAll(merge.getDiffModel());
//        return buildMapModel(qTable, mapModels);
//    }
//
//    //todo
//    public static MapModel onlyDiffModels(
//            RelationalPath<?> qTable, MapModel origin, MapModel merge) {
//        Assert.notNull(origin);
//        Assert.notNull(merge);
//        Map<Path<?>, Object> mapModels = new HashMap<>();
//        origin.getDiffModel().entrySet().stream().filter(entry -> merge.contains(entry.getKey())
//                && ObjectUtils.notEqual(merge
//                .getColumnValue(entry.getKey(), Object.class), entry.getValue())
//                && !ModelHelper.isPrimaryKey(entry.getKey())).forEach(entry -> {
//            mapModels.put(entry.getKey(), merge.getColumnValue(entry.getKey(), Object.class));
//        });
//
//        return buildMapModel(qTable, mapModels);
//    }
}
