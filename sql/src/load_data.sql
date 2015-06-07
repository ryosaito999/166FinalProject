
COPY USR
FROM '/home/csmajs/rsait001/final166/data/usr.csv'
WITH DELIMITER ';'
CSV HEADER;

COPY WORK_EXPR
FROM '/home/csmajs/rsait001/final166/data/work_ex.csv'
WITH DELIMITER ';'
CSV HEADER;


COPY EDUCATIONAL_DETAILS
FROM '/home/csmajs/rsait001/final166/data/edu_det.csv'
WITH DELIMITER ';'
CSV HEADER;


COPY CONNECTION_USR
FROM '/home/csmajs/rsait001/final166/data/connection.csv'
WITH DELIMITER ';'
CSV HEADER;


COPY MESSAGE
FROM '/home/csmajs/rsait001/final166/data/msg.csv'
WITH DELIMITER ','
CSV HEADER;

