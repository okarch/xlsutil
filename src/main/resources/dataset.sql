--
-- Creates the Dataset inventory database
--


--
-- t_dataset_tracker
--   tracks changes to datasets, uploads etc.
--   previd referes to the previous state's trackid
--   item describes the context (group, study etc.)
--   content holds an xml representation of the items to be tracked
--
drop table if exists t_dataset_tracker;
create table t_dataset_tracker(
  trackid   bigint primary key,
  modified  timestamp,
  previd    bigint,
  item      varchar(30),
  activity  varchar(50),
  uid       bigint,
  remark    varchar(254),
  content   text
); 
create index i_dstra_pid on t_dataset_tracker (previd);
create index i_dstra_ite on t_dataset_tracker (item);
create index i_dstra_uid on t_dataset_tracker (uid);

--
-- t_dataset_user
--   dataset user
--
drop table if exists t_dataset_user;
create table t_dataset_user(
  userid    bigint primary key,
  muid      varchar(20),
  username  varchar(80),
  apikey    varchar(20),
  email     varchar(80),
  active    varchar(5),
  roles     bigint,
  created   timestamp
);
create index i_dsus_muid on t_dataset_user (muid);
create index i_dsus_una on t_dataset_user (username);
create index i_dsus_api on t_dataset_user (apikey);
create index i_dsus_ema on t_dataset_user (email);

insert into t_dataset_user values( 0, 'm01061', 'Oliver Karch', '220466', 'Oliver.K.Karch@merckgroup.com', 'true', 1, '2015-02-20 01:01:01' );
insert into t_dataset_user values( 1, 'guest', 'Guest', '', 'bmdm@merckgroup.com', 'true', 2, '2015-02-20 01:01:01' );

--
-- t_dataset_organization
--   dataset sender / receiver / processor
--
drop table if exists t_dataset_organization;
create table t_dataset_organization(
  orgid       bigint primary key,
  orgname     varchar(50),
  siteid      varchar(20),
  countryid   smallint(5) unsigned,
  orgtype     varchar(50)
);
create index i_dsor_nam on t_dataset_organization (orgname);
create index i_dsor_sit on t_dataset_organization (siteid);
create index i_dsor_cou on t_dataset_organization (countryid);
create index i_dsor_typ on t_dataset_organization (orgtype);

insert into t_dataset_organization (orgid,orgname,countryid,orgtype) values( 1, 'Unknown', 0, 'unknown' );
insert into t_dataset_organization (orgid,orgname,countryid,orgtype) values( 2, 'Merck Biopharma', 0, 'sponsor' );

--
-- Dataset upload related tables
--

--
-- t_upload_template
--   the upload template
-- 
drop table if exists t_upload_template;
create table t_upload_template(
  templateid     bigint primary key,
  templatename   varchar(80),
  template       text,
  trackid        bigint
);
create index i_uplt_tna on t_upload_template (templatename);

--
-- t_upload_batch
--   the upload (raw) content typically delimited text
-- 
drop table if exists t_upload_batch;
create table t_upload_batch(
  uploadid       bigint primary key,
  templateid     bigint,
  uploaded       timestamp,
  userid         bigint,
  md5sum         varchar(32),
  filename       varchar(255),
  mime           varchar(80),
  filesetid      bigint
);
create index i_uplb_tid on t_upload_batch (templateid);
create index i_uplb_uid on t_upload_batch (userid);
create index i_uplb_md5 on t_upload_batch (md5sum);
create index i_uplb_fid on t_upload_batch (filesetid);

--
-- t_upload_raw
--   the upload (raw) content typically delimited text which
--   was moved to the archival area in case of multiple time
--   upload
-- 
drop table if exists t_upload_raw;
create table t_upload_raw(
  md5sum         varchar(32) primary key,
  upload         longtext
);

--
-- t_upload_output
--   captures the output of the upload
-- 
drop table if exists t_upload_output;
create table t_upload_output(
  outputid       bigint primary key,
  uploadid       bigint,
  created        timestamp,
  md5sum         varchar(32),
  filename       varchar(255),
  mime           varchar(80)
);
create index i_upout_uid on t_upload_output (uploadid);
create index i_upout_md5 on t_upload_output (md5sum);

drop table if exists t_upload_log;
create table t_upload_log(
  logid        bigint primary key, 
  uploadid     bigint, 
  logstamp     timestamp, 
  level        varchar(10), 
  line         bigint, 
  message      varchar(255) 
);
create index i_log_uid on t_upload_log( uploadid );
create index i_log_lst on t_upload_log( logstamp );
create index i_log_lev on t_upload_log( level );
create index i_log_lin on t_upload_log( line );


--
-- Dataset related tables
--

--
-- t_dataset
--   the dataset registry
--
drop table if exists t_dataset;
create table t_dataset(
  datasetid   varchar(36) primary key,
  study       varchar(50),
  setname     varchar(128),
  datatype    varchar(128),
  created     timestamp,
  version     varchar(20),
  stamp       bigint,
  trackid     bigint
);
create index i_ds_sna on t_dataset (study);
create index i_ds_sty on t_dataset (setname);
create index i_ds_tid on t_dataset (trackid);

--
-- t_dataset_step
--   the processing step
--
drop table if exists t_dataset_step;
create table t_dataset_step(
  stepid      bigint primary key,
  step        varchar(80),
  stepdesc    varchar(254)
);
create index i_dss_step on t_dataset_step (step);

insert into t_dataset_step (stepid, step) values ( 0, 'unknown' );
insert into t_dataset_step (stepid, step) values ( 1, 'setup' );
insert into t_dataset_step (stepid, step) values ( 2, 'send' );
insert into t_dataset_step (stepid, step) values ( 3, 'receive' );
insert into t_dataset_step (stepid, step) values ( 4, 'upload' );
insert into t_dataset_step (stepid, step) values ( 5, 'check' );
insert into t_dataset_step (stepid, step) values ( 6, 'release' );
insert into t_dataset_step (stepid, step) values ( 7, 'delay' );
insert into t_dataset_step (stepid, step) values ( 8, 'done' );

--
-- t_dataset_time
--   the timepoint for which something has to happen to a dataset
--
drop table if exists t_dataset_time;
create table t_dataset_time(
  timeid      bigint primary key,
  orgid       bigint,
  timepoint   varchar(50),
  timetype    varchar(20),      
  expdt       timestamp
);
create index i_dsti_oid on t_dataset_time (orgid);
create index i_dsti_vis on t_dataset_time (timepoint);

--
-- t_dataset_processing
--   captures how the dataset has been processed 
--   (optionally linked to a specific step)
--
drop table if exists t_dataset_processing;
create table t_dataset_processing(
  datasetid   varchar(36),
  stepid      bigint,
  timeid      bigint,
  step        int,
  processed   timestamp,
  remark      varchar(128),
  trackid     bigint
);
create index i_dspc_sid on t_dataset_processing (datasetid);
create index i_dspc_trd on t_dataset_processing (stepid);
create index i_dspc_tmd on t_dataset_processing (timeid);
create index i_dspc_pro on t_dataset_processing (processed);
create index i_dspc_tid on t_dataset_processing (trackid);

--
-- General property tables
--

--
-- t_property
--   holds property definitions
--
drop table if exists t_property;
create table t_property( 
  propertyid   bigint primary key, 
  propertyname varchar(50), 
  label        varchar(80), 
  typeid       bigint,
  trackid      bigint
);
create index i_prop_name on t_property (propertyname);
create index i_prop_tyid on t_property (typeid);
create index i_prop_trid on t_property (trackid);

--
-- t_property_type
--   represents the type of a property
--
drop table if exists t_property_type;
create table t_property_type( 
  typeid       bigint primary key, 
  typename     varchar(50), 
  label        varchar(80)
);
create index i_ptype_nam on t_property_type (typename);

--
-- basic parameter types
--
insert into t_property_type values( 0, 'unknown', 'Unknown' ); 
insert into t_property_type values( 1, 'text', 'Text' ); 

--
-- specific list types
--
insert into t_property_type values( 2, 'header', 'Column header' ); 

--
-- t_property_list
--   container to handles sets of properties
--
drop table if exists t_property_list;
create table t_property_list( 
  listid       bigint primary key,
  listname     varchar(80),
  typeid       bigint
);
create index i_plist_nam on t_property_list (listname);
create index i_plist_typ on t_property_list (typeid);

--
-- t_property_member
--   assigns a property to a list of properties
--
drop table if exists t_property_member;
create table t_property_member( 
  listid       bigint,
  propertyid   bigint,
  rank         int, 
  display      varchar(5)
);
create index i_pmem_lid on t_property_member (listid);
create index i_pmem_pid on t_property_member (propertyid);
create index i_pmem_rank on t_property_member (rank);

--
-- t_property_column
--   extended column properties of an attribute
--
drop table if exists t_property_column;
create table t_property_column( 
  columnid     bigint primary key,
  propertyid   bigint,
  dbformat     varchar(50),
  informat     varchar(254),
  outformat    varchar(254),
  columnsize   int,
  digits       int,
  minoccurs    int,
  maxoccurs    int,
  mandatory    varchar(5)
);
create index i_pcol_pid on t_property_column (propertyid);


--
-- Data transfer related tables
--

--
-- t_dts_specification
--   the data transfer specification
--
drop table if exists t_dts_specification;
create table t_dts_specification(
  dtsid       bigint primary key,
  datasetid   varchar(36),
  listid      bigint,
  senderid    bigint,
  receiverid  bigint,    
  created     timestamp,
  previousid  bigint,
  version     varchar(20),
  stamp       bigint,
  trackid     bigint
);
create index i_dts_spec_dsid on t_dts_specification ( datasetid );
create index i_dts_spec_lid on t_dts_specification ( listid );
create index i_dts_spec_scro on t_dts_specification ( senderid );
create index i_dts_spec_rcro on t_dts_specification ( receiverid );
create index i_dts_spec_prev on t_dts_specification ( previousid );
create index i_dts_spec_tid on t_dts_secification (trackid);

