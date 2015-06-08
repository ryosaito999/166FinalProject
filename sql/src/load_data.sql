	
COPY USR
FROM '/home/csmajs/rlaw001/166FinalProject/data/usr.csv'
WITH DELIMITER ';'
CSV HEADER;

COPY WORK_EXPR
FROM '/home/csmajs/rlaw001/166FinalProject/data/work_ex.csv'
WITH DELIMITER ';'
CSV HEADER;


COPY EDUCATIONAL_DETAILS
FROM '/home/csmajs/rlaw001/166FinalProject/data/edu_det.csv'
WITH DELIMITER ';'
CSV HEADER;


COPY CONNECTION_USR
FROM '/home/csmajs/rlaw001/166FinalProject/data/connection.csv'
WITH DELIMITER ';'
CSV HEADER;


COPY MESSAGE
FROM '/home/csmajs/rlaw001/166FinalProject/data/msg.csv'
WITH DELIMITER ','
CSV HEADER;

