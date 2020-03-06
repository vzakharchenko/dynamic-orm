package com.github.vzakharchenko.dynamic.orm.structure;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.structure.exception.EmptyPathToChangeSets;
import com.github.vzakharchenko.dynamic.orm.structure.exception.UpdateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 12.04.15
 * Time: 22:30
 */
public class DbStructureServiceImpl implements DbStructureService,
        ApplicationListener<ContextRefreshedEvent> {

    private DBStructure daoStructureSaver;

    private DataSource dataSource;

    private String pathToChangeSets;

    @Override
    @Transactional
    public void save() {
        daoStructureSaver.save(dataSource);
    }

    @Override
    @Transactional
    public void load() {
        try {
            daoStructureSaver.update(dataSource, "*");
        } catch (UpdateException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    @Transactional
    public void clear() {
        try {
            daoStructureSaver.dropAll(dataSource);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    @Transactional
    public void unlock() {
        daoStructureSaver.unlock(dataSource);
    }

    @Override
    public String generateSql() {
        try {
            return daoStructureSaver.getSqlScript(dataSource);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setPathToChangeSets(String pathToChangeSets) {
        this.pathToChangeSets = pathToChangeSets;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (StringUtils.isEmpty(pathToChangeSets)) {
            throw new EmptyPathToChangeSets();
        }
        SimpleDbStructure springStructureSaver = new SimpleDbStructure();
        springStructureSaver.setPathToChangeSets(pathToChangeSets);
        daoStructureSaver = springStructureSaver;
        OrmQueryFactory ormQueryFactory = event
                .getApplicationContext().getBean(OrmQueryFactory.class);
        boolean started = ormQueryFactory.transactionManager().startTransactionIfNeeded();
        try {
            load();
        } catch (RuntimeException e) {
            if (started) {
                ormQueryFactory.transactionManager().rollback();
            }
            throw e;
        }
        ormQueryFactory.transactionManager().commit();
    }
}
