-- OBJECTS add AND (...) to filter rows
OBJECTS==SELECT object_name
FROM all_objects
WHERE ( object_name NOT LIKE '%$%' AND object_name NOT LIKE '%#%' )
  AND object_type = :type
  AND owner = :owner
  #NAME#
ORDER BY object_name
-- FUNCTION
FUNCTION==SELECT text
FROM all_source
WHERE type  = 'FUNCTION'
  AND name  = :name
  AND owner = :owner
ORDER BY line
-- PACKAGE
PACKAGE==SELECT text
FROM all_source
WHERE type  = 'PACKAGE'
  AND name  = :name
  AND owner = :owner
ORDER BY line
-- PROCEDURE
PROCEDURE==SELECT text
FROM all_source
WHERE type  = 'PROCEDURE'
  AND name  = :name
  AND owner = :owner
ORDER BY line
-- TRIGGER
TRIGGER==SELECT text
FROM all_source
WHERE type  = 'TRIGGER'
  AND name  = :name
  AND owner = :owner
ORDER BY line
-- TYPE
TYPE==SELECT text
FROM all_source
WHERE type  = 'TYPE'
  AND name  = :name
  AND owner = :owner
ORDER BY line
-- TABLE
TABLE==SELECT t.tablespace_name Tablespace,
  t.logging,
  t.temporary,
  DECODE( t.iot_type, NULL, 'HEAP', 'IOT', 'INDEX', t.iot_type ) AS organization,
  c.comments
FROM all_tables t
  JOIN all_tab_comments c ON c.table_name = t.table_name
WHERE t.table_name  = :name
  AND t.owner = :owner
-- TABLE COLUMNS
TABLE_COLUMNS==SELECT utc.column_name name,
  CASE utc.data_type
    WHEN 'NUMBER'   THEN utc.data_type || DECODE( utc.data_precision, NULL, NULL, '('|| utc.data_precision ||','|| utc.data_scale ||')' )
    WHEN 'VARCHAR2' THEN utc.data_type ||'('|| utc.data_length ||')'
    ELSE utc.data_type
  END type,
  utc.nullable,
  utc.data_default defaults,
  ucc.comments
FROM all_tab_columns utc
LEFT JOIN all_col_comments ucc ON ( 
      utc.table_name  = ucc.table_name 
  AND utc.column_name = ucc.column_name )
WHERE utc.table_name  = :name
  AND utc.owner = :owner
ORDER BY column_id
-- TABLE CONSTRAINTS
TABLE_CONSTRAINTS==SELECT uc.constraint_name name,
  DECODE( uc.constraint_type, 'C', 'CHECK', 'O', 'READ ONLY', 'P', 'PRIMARY KEY', 'R', 'FOREIGN KEY', 'U', 'UNIQUE', 'V', 'CHECK OPTION',  uc.constraint_type ) AS type,
  '' columns,
  uc.r_constraint_name r_name,
  uc2.owner ||'. '|| uc2.table_name ||'. '|| uc.r_constraint_name AS reference
FROM all_constraints uc
  LEFT JOIN all_constraints uc2 ON( uc2.owner = uc.owner AND uc2.constraint_name = uc.r_constraint_name )
WHERE uc.constraint_type != '?'
  AND uc.table_name       = :name
  AND uc.owner = :owner
ORDER BY uc.constraint_name
-- TABLE CONSTRAINTS COLUMNS
TABLE_CONSTRAINTS_COLUMNS==SELECT column_name
FROM all_cons_columns
WHERE table_name      = :name
  AND constraint_name = :other_name
  AND owner = :owner
ORDER BY position
-- TABLE INDEXES
TABLE_INDEXES==SELECT index_name name,
  index_type type,
  '' columns,
  uniqueness,
  tablespace_name tablespace
FROM all_indexes di
WHERE table_name = :name
  AND index_name NOT LIKE '%$%'
  AND owner = :owner
ORDER BY index_name
-- TABLE INDEXES COLUMNS
TABLE_INDEXES_COLUMNS==SELECT column_name
FROM all_ind_columns
WHERE table_name  = :name
  AND index_name  = :other_name
  AND table_owner = :owner
ORDER BY column_position
-- QUEUE
QUEUE==SELECT uq.queue_table,
  uq.queue_type type,
  uq.max_retries,
  uq.all_comment comments,
  uq.enqueue_enabled,
  uq.dequeue_enabled,
  uqt.object_type
FROM all_queues uq
  JOIN all_queue_tables uqt ON uqt.queue_table = uq.queue_table
WHERE uq.name = :name
  AND uq.owner = :owner
-- SCHEDULER JOBS
SCHEDULER_JOBS==SELECT job_type type,
  job_action action,
  schedule_name name,
  repeat_interval,
  auto_drop,
  restartable,
  comments
FROM all_scheduler_jobs
WHERE job_name = :name
  AND owner = :owner
-- SEQUENCE
SEQUENCE==SELECT min_value,
  max_value,
  increment_by,
  cache_size,
  cycle_flag,
  order_flag
FROM all_sequences
WHERE sequence_name = :name
  AND sequence_owner = :owner
-- TABLESPACE
TABLESPACE==SELECT default_tablespace,
  temporary_tablespace,
  account_status
FROM all_users
  AND username = :owner
-- JOBS
JOBS==SELECT log_user,
  priv_user,
  schema_user,
  interval,
  what
FROM all_jobs
WHERE job = :name
  AND schema_user = :owner
