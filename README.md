# Dynamic Orm
[![CircleCI](https://circleci.com/gh/vzakharchenko/dynamic-orm.svg?style=svg)](https://circleci.com/gh/vzakharchenko/dynamic-orm)
[![Coverage Status](https://coveralls.io/repos/github/vzakharchenko/dynamic-orm/badge.svg?branch=master)](https://coveralls.io/github/vzakharchenko/dynamic-orm?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/5c587a6e77be5e8cbef0/maintainability)](https://codeclimate.com/github/vzakharchenko/dynamic-orm/maintainability)

# Features
  - modify database structure on runtime (use Liquibase)
    - create tables
    - add/modify columns
    - add/remove indexes
    - add/remove foreign keys
    - etc...
  - crud operation on dynamic structures
    - insert
    - update
    - delete (soft delete)
    - support optimistic locking (Version column)
  - quering to dynamic structures
    - select
    - subqueries
    - union
    - join
  - cache operation
    - based on spring cache
    - Transaction and External(ehcache, infinispan, redis, etc) cache
    - cache queries based on Primary Key, Column, and Column and Values
    - synchronization cache with crud operations  


