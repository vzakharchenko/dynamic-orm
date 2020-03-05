package com.github.vzakharchenko.dynamic.orm.generator;


import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import com.sun.codemodel.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.util.Assert;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by vzakharchenko on 19.08.14.
 */
public class GenerateModelFactory extends AbstractGenerateModelFactory {


    public static RelationalPath<?> getQTable(Class<? extends RelationalPath> qTableClass) {
        Field staticField = getField(qTableClass);
        try {
            return (RelationalPath) staticField.get(qTableClass);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static Field getField(Class<? extends RelationalPath> qTableClass) {
        Field[] fields = qTableClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(
                    field.getModifiers()) && Objects.equals(field.getType(), qTableClass)) {
                return field;
            }
        }
        throw new IllegalStateException(qTableClass + " does not have static field");
    }

    @Override
    public void initialization(
            String modelPackage, String modelsPath, List<Class<?>> classList) {

    }

    @Override
    public void finalization(String modelPackage, String modelsPath) {

    }

    @Override
    public void generate(
            Class<? extends RelationalPath<?>> aclass, String name,
            String modelPackage, String modelsPath) throws Exception {
        System.out.println("class " + aclass.getName());
        RelationalPath<?> qTable = getQTable(aclass);
        File directory = new File(modelsPath, StringUtils
                .replaceChars(modelPackage, '.', '/'));
        JCodeModel codeModel = new JCodeModel();
        JDefinedClass definedClass = codeModel
                ._class(modelPackage + "." + name);
        JDocComment jDocComment = definedClass.javadoc();
        jDocComment.add(message);
        File javaFile = new File(directory, name + ".java");
        if (javaFile.exists()) {
            Assert.isTrue(FileUtils.deleteQuietly(javaFile));
        }

        definedClass
                .annotate(QueryDslModel.class)
                .param("qTableClass", aclass)
                .param("tableName", qTable.getTableName());
        definedClass._implements(DMLModel.class);

        List<Path<?>> columns = qTable.getColumns();
        // generate body
        for (Path<?> column : columns) {
            String fieldName = ModelHelper.getColumnName(column);
            System.err.println("-> field " + fieldName);
            Class<?> type = column.getType();
            JFieldVar jFieldVar = definedClass.field(JMod.PRIVATE, type, fieldName);

            JMethod getMethod = definedClass.method(
                    JMod.PUBLIC, type, "get" + WordUtils.capitalize(fieldName));
            JMethod setMethod = definedClass.method(
                    JMod.PUBLIC, Void.TYPE, "set" + WordUtils.capitalize(fieldName));


            JFieldRef jFieldRef = JExpr._this().ref(fieldName);
            if (Date.class.isAssignableFrom(column.getType())) {
                JClass dateClass = codeModel.ref(column.getType());

                JConditional getConditional = getMethod
                        .body()._if(jFieldVar.ne(JExpr._null()));
                getConditional._then()._return(JExpr
                        .cast(dateClass, jFieldVar.invoke("clone")));
                getConditional._else()._return(JExpr._null());

                JVar jVar = setMethod.param(type, fieldName);
                JConditional setConditional = setMethod.body()._if(jVar.ne(JExpr._null()));
                setConditional._then().assign(jFieldRef, JExpr
                        .cast(dateClass, JExpr.invoke(jVar, "clone")));
                setConditional._else().assign(jFieldRef, JExpr._null());
            } else {
                getMethod.body()._return(jFieldVar);
                JVar jVar = setMethod.param(type, fieldName);
                setMethod.body().assign(jFieldRef, jVar);
            }


        }

        File destDir = new File(modelsPath);
        if (!destDir.exists()) {
            Assert.isTrue(destDir.mkdirs());
        }
        codeModel.build(destDir);
    }
}
