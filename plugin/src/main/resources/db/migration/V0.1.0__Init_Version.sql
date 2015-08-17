CREATE TABLE IF NOT EXISTS BUILDS (
	application_name varchar(255) NOT NULL, 
	build_version varchar(255) NOT NULL, 
	build_url varchar(255) NOT NULL, 
	build_number long, 
	built_at long, 
	PRIMARY KEY(application_name, build_version)
);

CREATE TABLE IF NOT EXISTS DEPLOYMENTS (
	key long auto_increment, 
	application_name varchar(255) NOT NULL, 
	build_version varchar(255) NOT NULL, 
	environment_key varchar(255) NOT NULL, 
	deployed_at long, 
	PRIMARY KEY(key)
);

CREATE TABLE IF NOT EXISTS ENVIRONMENTS (
	key varchar(255) NOT NULL primary key, 
	title varchar(255)
);